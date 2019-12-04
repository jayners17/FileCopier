import jdk.internal.util.EnvUtils;
import org.omg.CORBA.Environment;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class MergeSortBuffered {

    public static RandomAccessFile mergeFile, file1, file2;
    public static RandomFileBuffer2 bufferBlock1, bufferBlock2;
    public static final URL FILE = CreateFiles.class.getResource("/mergeFile.dat");
    public static final URL FILE1 = CreateFiles.class.getResource("/fileOne.dat");
    public static final URL FILE2 = CreateFiles.class.getResource("/fileTwo.dat");
    public static final int SIZE = 8 * 32;
    public static final int AMT_OF_INTEGERS = SIZE / 32;

    public static void main(String[] args) throws IOException {
        File fileOne = null;
        File file = null;
        File fileTwo = null;
        String dir = System.getProperty("user.dir");
        file = new File(dir + "/mergeFile.dat");
        fileOne = new File(dir + "/fileOne.dat");
        fileTwo = new File(dir + "/fileTwo.dat");

        //Files are located in out directory
        mergeFile = new RandomAccessFile(file, "rw");
        file1 = new RandomAccessFile(fileOne, "rw");
        file2 = new RandomAccessFile(fileTwo, "rw");

        //Fills file with random integers
        createFile();
        printFile();

        //Creates buffers
        bufferBlock1 = new RandomFileBuffer2(mergeFile, SIZE/2, "Block1");
        bufferBlock2 = new RandomFileBuffer2(mergeFile, SIZE/2, "Block2");

        printFile();

        mergeFile.close();
        file1.close();
        file2.close();
    }

    public static void mergeSort() throws IOException {

    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.write(random.nextInt(256));
        }
        mergeFile.seek(0);
    }

    public static void printFile() throws IOException {
        mergeFile.seek(0);
        System.out.print("{ ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(mergeFile.read() + ", ");
        }
        System.out.print(mergeFile.read() + " }\n");
    }


}
