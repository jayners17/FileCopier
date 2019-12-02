import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;

public class RandomFileBuffer2 {

    private RandomAccessFile file;
    private String name;
    private int BUFFER_SIZE;
    private int length;
    private byte[] buffer;
    private int currR;
    private int currW;

    private boolean autoFlush;

    public RandomFileBuffer2(RandomAccessFile file, int size, String name, boolean autoFlush) {
        this.file = file;
        BUFFER_SIZE = size;
        buffer = new byte[BUFFER_SIZE];
        length = 0;
        currR = 0;
        currW = 0;
        this.name = name;
        this.autoFlush = autoFlush;
    }
    
    public RandomFileBuffer2(RandomAccessFile file, int size, String name) {
        this(file, size, name, true);
    }
    
    /**
     * Fills the buffer using bytes read from the connected file.
     */
    public void fill() {
        int n = 0;

        try {
            n = file.read(buffer);
        } catch (EOFException eof) {
            //Ignore
        } catch (IOException ioe) {
            System.err.println("Error in fillBuffer()");
            ioe.printStackTrace();
        }

        length = n;
        currR = currW = 0;

    }
    
    
    /**
     * Writes all of the bytes from the internal buffer to the file and resets
     * the internal pointers.
     * @throws IOException
     */
    public void writeToFile() throws IOException {
        int count = 0;

        file.write(buffer);
        length = 0;
        currR = currW = 0;

    }


    public void append(int value) throws IOException {
        if (full()) {
            writeToFile();
        }

        byte b1 = (byte) (value >> 24);
        byte b2 = (byte) ((value >> 16));
        byte b3 = (byte) ((value >> 8));
        byte b4 = (byte) ((value));

        buffer[currW++] = b1;
        length++;

        buffer[currW++] = b2;
        length++;

        buffer[currW++] = b3;
        length++;

        buffer[currW++] = b4;
        length++;
    }
    
    /**
     * Appends the given value to the internal byte buffer, writing the buffer to the
     * underlying file once it is full.
     * @param value the value to be written
     * @throws IOException
     */
    public boolean append(byte value) throws IOException {
        if (full()) {
            if(autoFlush){
                writeToFile();
            }else {
                return false;
            }
        }

        buffer[currW++] = value;
        length++;
        return true;
    }
    
    /**
     * Reads 4 bytes from the underlying file and returns them as an integer.
     * @return the integer read
     */
    public int read() {
        if (empty()) {
            fill();
        }

        int b1 = Math.abs(buffer[currR++]);
        int b2 = Math.abs(buffer[currR++]);
        int b3 = Math.abs(buffer[currR++]);
        int b4 = Math.abs(buffer[currR++]);

        int intValue = b1 << 24;
        intValue |= b2 << 16;
        intValue |= b3 << 8;
        intValue |= b4;

        return intValue;
    }

    public byte read(byte b) {
        if (empty()) {
            fill();
        }

        return ((byte) Math.abs(buffer[currR++]));
    }
    
    /**
     * Clears the buffer by resetting the internal pointers.
     */
    public void clear() throws IOException {
        currR = currW = length = 0;
    }
    
    /**
     * Resets the file pointer of the underlying {@link RandomAccessFile} to the
     * beginning of the file.
     * @throws IOException
     */
    public void reset() throws IOException {
        file.seek(0L);
    }
    
    /**
     * Returns the size of this buffer.
     * @return the size of this buffer.
     */
    public int getBufferSize() {
        return BUFFER_SIZE;
    }
    
    /**
     * Returns the number of bytes stored in this buffer
     * @return the number of bytes stored in this buffer.
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Returns whether or not this buffer is empty.
     * @return {@code true}, if this buffer is empty<br>{@code false}, if otherwise
     */
    public boolean empty() {
        return currR == length;
    }


    public boolean full() {
        return length >= BUFFER_SIZE * 4;
    }

    private String getName() {
        return name;
    }
    
    /**
     * Writes any remaining bytes in this buffer to the underlying {@link RandomAccessFile}
     * @throws IOException
     */
    public void flush() throws IOException {
        writeToFile();
    }


}
