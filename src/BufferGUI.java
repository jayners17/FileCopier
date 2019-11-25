import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class BufferGUI {

    public static RandomAccessFile original, copy;
    private static Dimension frameSize = new Dimension(560, 130);
    public RandomFileBuffer2 buff;
    private File[] files;

    public static void main(String[] args) {
        new BufferGUI();
    }


    public BufferGUI() {
        //Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        //Init FileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Copy");
        fileChooser.setMultiSelectionEnabled(true);
        
        JFrame frame = new JFrame("Copy File");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        
        //File search area
        GridLayout layout_grid = new GridLayout(1, 3);
        layout_grid.setHgap(8);
        
        
        JPanel panel_FileField = new JPanel(layout_grid);
        
        JTextField field_FileName = new JTextField(125);
        field_FileName.setEditable(false);
        
        panel_FileField.add(field_FileName);
        
        //Add browse button
        JButton bttn_Browse = new JButton("Browse...");
        bttn_Browse.setText("Browse...");
        //bttn_Browse.setAction(new BrowseAction(fileChooser, frame, field_FileName));
        bttn_Browse.setMinimumSize(new Dimension(125, 23));
        bttn_Browse.setMaximumSize(new Dimension(125, 23));
        panel_FileField.add(Box.createRigidArea(new Dimension(8, 0)));
        panel_FileField.add(bttn_Browse);
        
        //Buttons
        
        javax.swing.
        
        JButton bttn_Confirm = new JButton("Copy");
        //Confirm Button is pressed
        bttn_Confirm.addActionListener(this::onConfirm);
        JButton bttn_Cancel = new JButton("Cancel");
        
        //Create Button Bar
        JPanel panel_bttnBar = new JPanel(true);
        panel_bttnBar.setLayout(new BoxLayout(panel_bttnBar, BoxLayout.LINE_AXIS));
        
        panel_bttnBar.add(Box.createHorizontalGlue());
        panel_bttnBar.add(bttn_Confirm);
        panel_bttnBar.add(Box.createRigidArea(new Dimension(8, 0)));
        panel_bttnBar.add(bttn_Cancel);
        
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(panel_FileField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        mainPanel.add(panel_bttnBar);
        
        frame.setContentPane(mainPanel);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
        System.out.println(bttn_Browse.getSize());
        System.out.println(bttn_Confirm.getSize());
        System.out.println(field_FileName.getSize());
    }
    
    private void onConfirm(ActionEvent e){
        if(files != null){
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
        }
    }
}
