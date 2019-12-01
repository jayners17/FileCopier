import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class MergeSortBuffered {

    public static RandomAccessFile mergeFile, output;
    public static RandomFileBuffer2 buffer, bufferBlock1, bufferBlock2, bufferBlock3, bufferBlock4;
    public static final URL FILE = CreateFiles.class.getResource("/mergeFile.dat");
    public static final URL OUTPUT = CreateFiles.class.getResource("/outFile.dat");
    public static final int SIZE = 20 * 32;
    public static final int AMT_OF_INTEGERS = SIZE / 32;

    public static void main(String[] args) throws IOException {
        File file1 = null;
        File fileOut = null;

        try {
            file1 = new File(FILE.toURI());
            fileOut = new File(OUTPUT.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Failed to load file");
            System.exit(-1);
        }

        //Files are located in out directory
        mergeFile = new RandomAccessFile(file1, "rw");
        output = new RandomAccessFile(fileOut, "rw");

        //Fills file with random integers
        createFile();

        //Creates buffers
        buffer = new RandomFileBuffer2(output, SIZE, "Merge");
        bufferBlock1 = new RandomFileBuffer2(output, SIZE/4, "Block1");
        bufferBlock2 = new RandomFileBuffer2(output, SIZE/4, "Block2");
        bufferBlock3 = new RandomFileBuffer2(output, SIZE/4, "Block3");
        bufferBlock4 = new RandomFileBuffer2(output, SIZE/4, "Block4");



        sortBlock(bufferBlock1);
        sortBlock(bufferBlock2);
        sortBlock(bufferBlock3);
        sortBlock(bufferBlock4);


        mergeFile.close();
        output.close();
    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.write(random.nextInt());
        }
    }

    public static void sortBlock(RandomFileBuffer2 buff) throws IOException {
        int[] arr = new int[AMT_OF_INTEGERS/4];
        while (!buff.full()) {
            buff.append(mergeFile.readInt());
        }
        buff.flush();
        buff.fill();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = buff.read();
        }
        for (int i = 0; i < ; i++) {
            
        }



    }
}
