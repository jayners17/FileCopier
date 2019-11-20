import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;

public class RandomFileBuffer2
{

private RandomAccessFile file;
private String name;
private int BUFFER_SIZE;
private int length;
private byte [] buffer;
private int currR;
private int currW;


public RandomFileBuffer2(RandomAccessFile file, int size, String name) {
	this.file = file;
	BUFFER_SIZE = size;
	buffer = new byte[BUFFER_SIZE*4];
	length = 0;
	currR = 0;
	currW = 0;
	this.name = name;
}
	
// Fill empty buffer from File
public void fill()
{

int n = 0;

try {

n = file.read(buffer);
}

catch (EOFException eof) {   }
catch (IOException ioe) { System.err.println("Error in fillBuffer()"); ioe.printStackTrace();  }

length = n;
currR = currW = 0;

}


// Writes to the File from buffer
public void writeToFile() throws IOException
{

int count = 0;

file.write(buffer);
length = 0;
currR = currW = 0;

}


public void append(int value) throws IOException
{
	if (full())
	{
		writeToFile();
	}

	byte b1 = (byte)(value >> 24);
	byte b2 = (byte)((value >> 16));
	byte b3 = (byte)((value >> 8));
	byte b4 = (byte)((value));

	buffer[currW++] = b1;
	length++;

	buffer[currW++] = b2;
	length++;

	buffer[currW++] = b3;
	length++;

	buffer[currW++] = b4;
	length++;
}


public int read()
{
	if (empty()) 
	{
		fill();
	}

	int b1 =  Math.abs(buffer[currR++]);
	int b2 =  Math.abs(buffer[currR++]);
	int b3 =  Math.abs(buffer[currR++]);
	int b4 =  Math.abs(buffer[currR++]);

	int intValue = b1 << 24;
	intValue |= b2 << 16;
	intValue |= b3 << 8;
	intValue |= b4;

	return intValue;
}


public void clear() throws IOException
{
	currR = currW = length = 0;
}


public void reset() throws IOException
{
	file.seek(0L);
}


public int getBufferSize()
{
	return BUFFER_SIZE;
}

public int getLength()
{
	return length;
}

public boolean empty()
{
	return currR == length;
}


public boolean full()
{
	return length >= BUFFER_SIZE*4;
}

private String getName()
{
	return name;
}

public void flush() throws IOException
{
writeToFile();
}


}
