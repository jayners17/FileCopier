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

    public static RandomAccessFile mergeFile, output;
    public static RandomFileBuffer2 buffer, bufferBlock1, bufferBlock2, bufferBlock3, bufferBlock4;
    public static final URL FILE = CreateFiles.class.getResource("/mergeFile.dat");
    public static final URL OUTPUT = CreateFiles.class.getResource("/outFile.dat");
    public static final int SIZE = 20 * 32;
    public static final int AMT_OF_INTEGERS = SIZE / 32;

    public static void main(String[] args) throws IOException {
        File file1 = null;
        File fileOut = null;
        String dir = System.getProperty("user.dir");
        file1 = new File(dir + "/mergeFile.dat");
        fileOut = new File(dir + "/outFile.dat");

        //Files are located in out directory
        mergeFile = new RandomAccessFile(file1, "rw");
        output = new RandomAccessFile(fileOut, "rw");

        //Fills file with random integers
        createFile();
        printOriginalFile();


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


        printSortedFile();

        mergeFile.close();
        output.close();
    }

    public static void createFile() throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_INTEGERS; i++) {
            mergeFile.write(random.nextInt());
        }
        mergeFile.seek(0);
    }

    public static void sortBlock(RandomFileBuffer2 buff) throws IOException {
        /**
         * Fills the buffer and sorts it
         */
        try{
            mergeFile.seek(0);
            while (!buff.full()) {
                buff.append(mergeFile.readInt());
            }
        }catch (EOFException e){
            e.getMessage();
        }
        mergeSort(buff.getBuffer());
        buff.flush();

    }
/*    public static void mergeSort(byte[] a){
        if (a.length < 2){
            return;
        }
        int mid = a.length/2;
        byte[] left = new byte[mid];
        byte[] right = new byte[a.length - mid];



        for (int i = 0; i < mid; i++) {
            left[i] = (a[i]);
        }
        for (int i = mid; i < a.length ; i++) {
            right[i - mid] = a[i];
        }
        mergeSort(left);
        mergeSort(right);

        merge(a,left,right,mid,a.length - mid);
    }
    public static void merge(byte[] a, byte [] leftArray, byte[] rightArray, int left, int right){
        int i = 0;
        int j = 0;
        int k = 0;

        while(i < left && j < right){
            if (((int) leftArray[i]) <= ((int)rightArray[j])){
                a[k++] = leftArray[i++];
            }else{
                a[k++] = rightArray[j++];
            }
        }
        while(i < left){
            a[k++] = leftArray[i++];
        }
        while(j < right){
            a[k++] = rightArray[j++];
        }
    }*/
    public static void mergeSortReal()throws IOException{
        for (int pass = 0, count = 1; pass < maxPass;  pass++, count *=2){
            split(F,A,B,count);
            merge(F,A,B,count);
        }
    }
    private void split(RandomAccessFile F,RandomAccessFile A, RandomAccessFile B, int n)throws IOException{
        F.seek(0);
        A.seek(0);
        B.seek(0);

        for (int i = 0; i < size / (2 * n); i++) {
            for (int j = 0; j < n; j++) {
                A.writeInt(F.readInt());
            }
            for (int k = 0; k < n; k++) {
                B.writeInt(F.readInt());
            }
        }
    }
    private void merge(RandomAccessFile F, RandomAccessFile A, RandomAccessFile B, int n)throws  IOException{
        int a,b;
        int aCount = 0, bCount = 0;

        F.seek(0);
        A.seek(0);
        B.seek(0);

        for (int i = 0; i < size / (2 * n); i++) {
            aCount = bCount = n;
            a = A.readInt();
            b = B.readInt();

            while (aCount != 0 && bCount != 0){
                if ( a < b){
                    F.writeInt(a);
                    aCount--;

                    if (aCount != 0){
                        a = A.readInt();
                    }
                }else {
                    F.writeInt(b);
                    bCount--;

                    if (bCount != 0){
                        b = B.readInt();
                    }
                }
            }
            while(aCount-- != 0){
                F.writeInt(a);
                if (aCount != 0){
                    a = A.readInt();
                }
            }
            while (bCount-- != 0) {
                F.writeInt(b);
                if (bCount != 0) {
                    b = B.readInt();
                }
            }
        }
    }

    public static void printSortedFile() throws IOException {
        output.seek(0);
        System.out.print("{ ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(output.read() + ", ");
        }
        System.out.print(output.read() + " }\n");
    }

    public static void printOriginalFile() throws IOException {
        mergeFile.seek(0);
        System.out.print("{ ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(mergeFile.read() + ", ");
        }
        System.out.print(mergeFile.read() + " }\n");
    }


}
