import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class BufferGUI {

    public static RandomAccessFile original, copy;
    public RandomFileBuffer2 buff;

    public static void main(String[] args) {
        new BufferGUI();
    }


    public BufferGUI() {
        JFrame frame = new JFrame("Copy File");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320,150);
        frame.setLocationRelativeTo(null);
    
        BorderLayout layout = new BorderLayout(6, 6);
        JPanel mainPanel = new JPanel(layout, true);
        mainPanel.setMaximumSize(frame.getSize());
        JTextField field_FileName = new JTextField();
        field_FileName.setEditable(false);
    
        field_FileName.setMaximumSize(new Dimension(640, 25));
    
        JButton bttn_Confirm = new JButton("Copy");
        JButton bttn_Cancel = new JButton("Cancel");
        JButton bttn_Browse = new JButton("Browse...");
    
        bttn_Confirm.getMargin().set(6,6,6,8);
        bttn_Cancel.getMargin().set(6,8,6,6);
    
        mainPanel.add(field_FileName, BorderLayout.CENTER);
        JPanel bttnBar = new JPanel(true);
        BoxLayout bttnBar_Layout = new BoxLayout(bttnBar, BoxLayout.X_AXIS);
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        bttnBar.setLayout(bttnBar_Layout);
    
        bttnBar.add(bttn_Confirm);
        bttnBar.add(bttn_Cancel);
        mainPanel.add(bttnBar, BorderLayout.SOUTH);
        mainPanel.add(bttn_Browse ,BorderLayout.EAST);
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Copy");
        fileChooser.setMultiSelectionEnabled(true);
    
    
        bttn_Browse.addActionListener(l->{
            int result = fileChooser.showOpenDialog(mainPanel);
            switch (result){
                case JFileChooser.APPROVE_OPTION:
                    StringBuffer buff = new StringBuffer();
                    if(fileChooser.getSelectedFiles().length > 1){
                        buff.append(fileChooser.getSelectedFiles()[0].getPath());
                        buff.append(";");
                        for (int i = 1; i < fileChooser.getSelectedFiles().length; i++){
                            buff.append(fileChooser.getSelectedFiles()[i].getPath());
                            buff.append(";");
                        }
                        field_FileName.setText(buff.toString());
                    }else {
                        field_FileName.setText(fileChooser.getSelectedFile().getPath());
                    }
            }
        });
    
    
        //Button is pressed
        bttn_Confirm.addActionListener(e -> {
                File[] files = fileChooser.getSelectedFiles();
            
                for (int i = 0; i < files.length; i++) {
                    long startTime = System.nanoTime();
                
                    //Creates an original file and copy file
                    File selectedFile = files[i];
                    try {
                        original = new RandomAccessFile(selectedFile, "r");
                        copy = new RandomAccessFile("COPY_" +selectedFile.getName(), "rw");
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                    }
                
                    //Reads and writes bytes from original to copy file
                    try {
                    
                        long endTime = System.nanoTime();
                        long duration = endTime - startTime;
                        System.out.printf("File:\t%s%nSize:\t%f MB%nTime:\t%.4f seconds%n%n", selectedFile.getName(), (original.length()/10e5), duration/10e8);
                    
                        copy.close();
                        original.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
        });
    
        frame.setContentPane(mainPanel);
        frame.setMaximumSize(new Dimension(320, 150));
    
        frame.setVisible(true);
    }
}
