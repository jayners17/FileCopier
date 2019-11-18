import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class CreateFiles {

    public static final URL ONE = CreateFiles.class.getResource("/one.dat");
    public static final URL FIVE = CreateFiles.class.getResource("/five.dat");
    public static final URL TEN = CreateFiles.class.getResource("/ten.dat");

    public static void main(String[] args) throws IOException {
        //Open binary file for read/write operations
        File fileOne = null;
        File fileFive = null;
        File fileTen = null;

        try {
            fileOne = new File(ONE.toURI());
            fileFive = new File(FIVE.toURI());
            fileTen = new File(TEN.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Failed to load file");
            System.exit(-1);
        }

        //Files are located in out directory
        RandomAccessFile one = new RandomAccessFile(fileOne, "rw");
        RandomAccessFile five = new RandomAccessFile(fileFive, "rw");
        RandomAccessFile ten = new RandomAccessFile(fileTen, "rw");

        one.setLength(1000000);
        five.setLength(5000000);
        ten.setLength(10000000);

        one.close();
        five.close();
        ten.close();
    }

}
