import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FileCopyGUI {

    public static RandomAccessFile original, copy;

    public static void main(String[] args) throws IOException{
        new FileCopyGUI();
    }

    public FileCopyGUI() throws IOException {

        //JFrame stuff
        JFrame frame = new JFrame("Copy File");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750,750);
        frame.setLocationRelativeTo(null);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Copy");

        //Button is pressed
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int status = fileChooser.showOpenDialog(null);

                if (status == JFileChooser.APPROVE_OPTION) {

                    //Creates an original file and copy file
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        original = new RandomAccessFile(selectedFile, "r");
                        copy = new RandomAccessFile("COPY_" +selectedFile.getName(), "rw");
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                    }

                    //Reads and writes bytes from original to copy file
                    try {
                        byte[] bytes = new byte[(int)original.length()];
                        original.read(bytes);
                        copy.write(bytes);
                        copy.close();
                        original.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        frame.getContentPane().add(fileChooser);
        frame.setVisible(true);
    }

}
