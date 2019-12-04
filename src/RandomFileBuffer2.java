import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;
import java.util.Random;

public class RandomFileBuffer2 {

    private RandomAccessFile file;
    private String name;
    private int BUFFER_SIZE;
    private int length;
    private byte[] buffer;
    private int currR;
    private int currW;
    
    /**
     * Controls whether the buffer automatically writes all of it's bytes to the underlying
     * file.
     */
    private boolean autoFlush;
    
    /**
     * Constructs a new {@link RandomFileBuffer2} object to read and write to the {@link RandomAccessFile}
     * specified by the {@code file} argument. An internal byte array is used as the actual working buffer.
     * <br>
     * The {@code autoFlush} argument specifies whether or not this buffer automatically writes itself to
     * the underlying file once it's full. If set to {@code false}, this buffer will only write it's bytes
     * to the file when the {@link RandomFileBuffer2#flush()} method is called and, when this buffer is
     * full, any attempt to append to this buffer will fail.
     *
     * @param file the underlying {@link RandomAccessFile} this buffer operates on
     * @param size the size of this buffer
     * @param name the name of this buffer
     * @param autoFlush whether this buffer should automatically flush itself once it's full
     */
    public RandomFileBuffer2(RandomAccessFile file, int size, String name, boolean autoFlush) {
        this.file = file;
        BUFFER_SIZE = size * 4;
        buffer = new byte[BUFFER_SIZE];
        length = 0;
        currR = 0;
        currW = 0;
        this.name = name;
        this.autoFlush = autoFlush;
    }
    
    /**
     * Constructs a new {@link RandomFileBuffer2} object with {@code auto-flush} enabled.
     *
     * @param file the underlying {@link RandomAccessFile} this buffer operates on
     * @param size the size of this buffer
     * @param name the name of this buffer
     */
    public RandomFileBuffer2(RandomAccessFile file, int size, String name) {
        this(file, size, name, true);
    }
    
    /**
     * Returns a reference to this buffer's underlying byte array.
     * @return the underlying byte array of this buffer
     */
    public byte[] getBuffer(){
        return this.buffer;
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
     */
    private void writeToFile() {
        try {
            file.write(buffer);
        } catch (IOException e) {
            System.err.println("Error writing to file");
            e.printStackTrace();
        }
        
        length = 0;
        currR = currW = 0;

    }


    public void append(int value){
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
     * Appends the given value to the internal byte buffer. If {@code autoFlush} is
     * enabled and the buffer is full, the buffer will empty itself to the underlying
     * {@link RandomAccessFile} before appending the value. Otherwise, this method
     * will return false and the value will not be appended.
     *
     * @param value the value to be written
     * @return whether or not the operation was successful
     */
    public boolean append(byte value){
        if (full() && autoFlush){
            flush();
        }else if(full() && !autoFlush){
            return false;
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
     * @throws IOException throws exception if error occurs during seeking
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
        return length >= BUFFER_SIZE;
    }

    private String getName() {
        return name;
    }
    
    /**
     * Writes any remaining bytes in this buffer to the underlying {@link RandomAccessFile}.
     */
    public void flush(){
        writeToFile();
    }


}
