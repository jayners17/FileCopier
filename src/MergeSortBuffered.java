import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class MergeSortBuffered {

    public static RandomAccessFile mergeFile;
    public static RandomFileBuffer2 buffer, bufferBlock;
    public static final URL FILE = CreateFiles.class.getResource("/mergeFile.dat");
    public static final int SIZE = 20 * 32;
    public static final int AMT_OF_INTEGERS = SIZE / 32;

    public static void main(String[] args) throws IOException {
        File file1 = null;

        try {
            file1 = new File(FILE.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Failed to load file");
            System.exit(-1);
        }

        //Files are located in out directory
        mergeFile = new RandomAccessFile(file1, "rw");

        //Fills file with random integers
        createFile();

        //Creates buffers
        buffer = new RandomFileBuffer2(mergeFile, SIZE, "Merge");
        bufferBlock = new RandomFileBuffer2(mergeFile, SIZE/4, "Block");

        mergeSort();

        mergeFile.close();
    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.write(random.nextInt());
        }
    }

    public static void mergeSort() throws IOException {
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            if (bufferBlock.full()) {
                bufferBlock.writeToFile();
                bufferBlock.flush();
            }
            bufferBlock.append(mergeFile.readInt());
        }
    }
}
