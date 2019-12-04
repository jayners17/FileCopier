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
    public static RandomAccessFile A, B;
    public static RandomFileBuffer2 buffer, bufferBlock1, bufferBlock2, bufferBlock3, bufferBlock4;
    
    public static final int SIZE = 20 * Integer.BYTES;
    public static final int AMT_OF_INTEGERS = SIZE / Integer.BYTES;

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
            mergeFile.writeInt(random.nextInt(255) + 1);
        }
        mergeFile.seek(0);
    }

    public static void sortBlock(RandomFileBuffer2 buff) throws IOException {
        /*
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
    
    public static void mergeSort(byte[] a){
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
    }

    public static void printSortedFile() throws IOException {
        output.seek(0);
        System.out.print("Sorted: { ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(output.readUnsignedByte() + ", ");
        }
        System.out.print(output.readUnsignedByte() + " }\n");
    }

    public static void printOriginalFile() throws IOException {
        mergeFile.seek(0);
        System.out.print("Original: { ");
        for (int i = 0; i < AMT_OF_INTEGERS-1; i++) {
            System.out.print(mergeFile.readUnsignedByte() + ", ");
        }
        System.out.print(mergeFile.readUnsignedByte() + " }\n");
    }

    public static void buffMergeSort(RandomAccessFile input) throws IOException{
        int maxPass = input.length();
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
                a.append(input.readInt());
            }
            for (int k = 0; k < n; k++) {
                b.append(input.readInt());
            }
        }
    }
    
}
