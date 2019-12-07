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

    public static RandomAccessFile mergeFile, fileA, fileB;
    public static RandomFileBuffer2 buffA, buffB, buffF;


    public static final int SIZE = 8 * Integer.BYTES;
    public static final int AMT_OF_INTEGERS = SIZE / Integer.BYTES;

    public static void main(String[] args) throws IOException {
        File file = null;
        File file1 = null;
        File file2 = null;
        String dir = System.getProperty("user.dir");
        file = new File(dir + "/mergeFile.dat");
        file1 = new File(dir + "/aFile.dat");
        file2 = new File(dir + "/bFile.dat");



        //Files are located in out directory
        mergeFile = new RandomAccessFile(file, "rw");
        fileA = new RandomAccessFile(file1, "rw");
        fileB = new RandomAccessFile(file2, "rw");

        buffA = new RandomFileBuffer2(fileA, SIZE/2, "A");
        buffB = new RandomFileBuffer2(fileB, SIZE/2, "B");
        buffF = new RandomFileBuffer2(mergeFile, SIZE, "F");



        //Fills file with random integers
        createFile();
        printOriginalFile();

        //Sorts file
        buffMergeSort();

        //Prints sorted file
        printSortedFile();

        mergeFile.close();
        fileA.close();
        fileB.close();
    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.writeInt(random.nextInt());
        }
    }

    public static void printFile(RandomAccessFile file, int size) throws IOException {
        file.seek(0);
        for (int i = 0; i < size ; i++) {
            System.out.print(file.read() + " ");
        }
        file.seek(0);

    }

    public static void printSortedFile() throws IOException {
        mergeFile.seek(0);
        System.out.print("Sorted: { ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(mergeFile.read() + ", ");
        }
        System.out.print(mergeFile.read() + " }\n");
    }

    public static void printOriginalFile() throws IOException {
        mergeFile.seek(0);
        System.out.print("Original: { ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(mergeFile.read() + ", ");
        }
        System.out.print(mergeFile.read() + " }\n");
    }

    public static void buffMergeSort() throws IOException{
        int maxPass = (int)Math.round(Math.log(AMT_OF_INTEGERS) / Math.log(2));

        for(int pass = 0, count = 1; pass < maxPass; pass++, count += pass){
           spilt(count);
           merge(count);
       }
       
    }
    
    private static void merge(int n) throws IOException {
        mergeFile.seek(0);
        fileA.seek(0);
        fileB.seek(0);

        int a = 0, b = 0;
        int aCount = 0, bCount = 0;
        for (int i = 0; i < (AMT_OF_INTEGERS / (2 * n)); i++) {
            aCount = bCount = n;
            
            a = fileA.readInt();
            b = fileB.readInt();

            while (aCount != 0 && bCount != 0){
                if(a<b){
                    buffF.append(a);
                    aCount--;
                    if(aCount != 0){
                        a = fileA.readInt();
                    }
                }else{
                    buffF.append(b);
                    bCount--;
                    if(bCount != 0){
                        b = fileB.readInt();
                    }
                }
            }
            
            while (aCount-- != 0){
                buffF.append(a);
                if(aCount != 0){
                    a = fileA.readInt();
                }
            }
    
            while (bCount-- != 0){
                buffF.append(b);
                if(bCount != 0){
                    b = fileB.readInt();
                }
            }
            buffF.flush();
        }

        System.out.println("\nF");
        printFile(mergeFile, 8);

    }
    
    private static void spilt(int n) throws IOException {
        mergeFile.seek(0);
        fileA.seek(0);
        fileB.seek(0);

        for (int i = 0; i < AMT_OF_INTEGERS / (n * 2); i++) {
            for (int j = 0; j < n; j++) {
                //fileA.writeInt(mergeFile.readInt());
                buffA.append(mergeFile.readInt());

            }
            for (int k = 0; k < n; k++) {
                //fileB.writeInt(mergeFile.readInt());
                buffB.append(mergeFile.readInt());
            }
        }
        buffA.flush();
        buffB.flush();

        System.out.println("\nA");
        printFile(fileA, 4);
        System.out.println("\nB");
        printFile(fileB, 4);
    }
    
}
