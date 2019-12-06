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

    public static RandomAccessFile mergeFile;
    public static RandomAccessFile A, B;

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
        A = new RandomAccessFile(file1, "rw");
        B = new RandomAccessFile(file2, "rw");


        //Fills file with random integers
        createFile();
        printOriginalFile();

        //Sorts file
        buffMergeSort(mergeFile);

        //Prints sorted file
        printSortedFile();

        mergeFile.close();
        A.close();
        B.close();
    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.writeInt(random.nextInt());
        }
        mergeFile.seek(0);
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

    public static void buffMergeSort(RandomAccessFile input) throws IOException{
        int maxPass = (int)Math.round(Math.log(AMT_OF_INTEGERS) / Math.log(2));
        RandomFileBuffer2 buff1 = new RandomFileBuffer2(A, (int)input.length()/2, "A");
        RandomFileBuffer2 buff2 = new RandomFileBuffer2(B, (int)input.length()/2, "B");
        
        for(int pass = 0, count = 1; pass < maxPass; pass++, count +=2){
           spilt(input, buff1, buff2, count);
           merge(input, buff1, buff2, count);
       }
       
    }
    
    private static void merge(RandomAccessFile input, RandomFileBuffer2 A, RandomFileBuffer2 B, int n) throws IOException {
        int a, b;
        int aCount = 0, bCount = 0;
        for (int i = 0; i < SIZE / (2 * n); i++) {
            aCount = bCount = n;
            
            a = A.read();
            b = B.read();
            while (aCount != 0 && bCount != 0){
                if(a<b){
                    input.write(a);
                    aCount--;
                    if(aCount != 0){
                        a = A.read();
                    }
                }else{
                    input.write(b);
                    bCount--;
                    if(bCount != 0){
                        b = B.read();
                    }
                }
            }
            
            while (aCount-- != 0){
                input.writeInt(a);
                if(aCount != 0){
                    a = A.read();
                }
            }
    
            while (bCount-- != 0){
                input.writeInt(b);
                if(bCount != 0){
                    b = B.read();
                }
            }
        }
    }
    
    private static void spilt(RandomAccessFile input, RandomFileBuffer2 a, RandomFileBuffer2 b, int n) throws IOException {
        for (int i = 0; i < SIZE / (2 * n); i++) {
            for (int j = 0; j < n; j++) {
                a.append(input.read());
            }
            for (int k = 0; k < n; k++) {
                b.append(input.read());
            }
        }
    }
    
}
