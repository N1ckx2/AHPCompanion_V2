import com.oracle.net.Sdp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */
public class EditConfig extends JFrame implements ActionListener {
    private JPanel panel;
    private JButton save, exit, browseProgramPath, browseStoragePath, browseUSBPath, browseSDPath;
    private JTextField programPath, sourcePath, usbPath, sdPath;
    private JFileChooser fileChooser;
    private Configuration config;
    private JFrame gui;

    public EditConfig(Configuration c, JFrame g) {
        config = c;
        gui = g;
        fileChooser = new JFileChooser("./");
        fileChooser.setDialogTitle("Find Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        //Setting up JPanel
        panel = new JPanel(new FlowLayout());

        //Setting up components - TextFields
        programPath = new JTextField(30);
        sourcePath = new JTextField(30);
        usbPath = new JTextField(30);
        sdPath = new JTextField(30);

        programPath.setText(config.loadConfiguration("ahpdataPath"));
        sourcePath.setText(config.loadConfiguration("sourcePath"));
        usbPath.setText(config.loadConfiguration("usbPath"));
        sdPath.setText(config.loadConfiguration("sdPath"));

        //Setting up components - Buttons
        save = new JButton("Save");
        exit = new JButton("Exit");
        browseProgramPath = new JButton("Browse...");
        browseStoragePath = new JButton("Browse...");
        browseUSBPath = new JButton("Browse...");
        browseSDPath = new JButton("Browse...");

        save.addActionListener(this);
        exit.addActionListener(this);
        browseProgramPath.addActionListener(this);
        browseStoragePath.addActionListener(this);
        browseUSBPath.addActionListener(this);
        browseSDPath.addActionListener(this);

        //Adding components to panel
        panel.add(new JLabel("Path to AHP Data Folder:"));
        panel.add(programPath);
        panel.add(browseProgramPath);
        panel.add(new JLabel("Path to Source Folder  :"));
        panel.add(sourcePath);
        panel.add(browseStoragePath);
        panel.add(new JLabel("Path to USB Drive     :"));
        panel.add(usbPath);
        panel.add(browseUSBPath);
        panel.add(new JLabel("Path to SD Drive      :"));
        panel.add(sdPath);
        panel.add(browseSDPath);
        panel.add(save);
        panel.add(exit);

        //Setting up JFrame
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //makes the buttons look and feel like windows
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAutoRequestFocus(true);
        pack();
        setTitle("Edit Configuration");
        setContentPane(panel);
        setResizable(false);
        setSize(500, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed (ActionEvent e){
        if (e.getSource() == save) {
            //Saves configuration based on user input
            config.saveConfiguration("ahpdataPath", programPath.getText());
            config.saveConfiguration("sourcePath", sourcePath.getText());
            config.saveConfiguration("usbPath", usbPath.getText());
            config.saveConfiguration("sdPath", sdPath.getText());

            config.saveConfiguration("Initializer", "1"); //this will tell the program that settings have been inputted
            JOptionPane.showMessageDialog(this, "Saved!", "Save Successful", JOptionPane.PLAIN_MESSAGE); //tell user suh
        } else if (e.getSource() == exit) { //close this gui and bring back the other
            gui.setVisible(true);
            gui.setAutoRequestFocus(true);
            dispose();
        } else if (e.getSource() == browseProgramPath) {
            fileChooser.setVisible(true);
            int choice = fileChooser.showOpenDialog(this); //open the folder chooser
            if (choice == JFileChooser.APPROVE_OPTION){
                programPath.setText(fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == browseStoragePath) {
            fileChooser.setVisible(true);
            int choice = fileChooser.showOpenDialog(this); //open the folder chooser
            if (choice == JFileChooser.APPROVE_OPTION){
                sourcePath.setText(fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == browseUSBPath) {
            fileChooser.setVisible(true);
            int choice = fileChooser.showOpenDialog(this); //open the folder chooser
            if (choice == JFileChooser.APPROVE_OPTION){
                usbPath.setText(fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == browseSDPath) {
            fileChooser.setVisible(true);
            int choice = fileChooser.showOpenDialog(this); //open the folder chooser
            if (choice == JFileChooser.APPROVE_OPTION){
                sdPath.setText(fileChooser.getSelectedFile().toString());
            }
        }
    }
}
