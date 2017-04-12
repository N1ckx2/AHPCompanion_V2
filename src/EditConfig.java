import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */
public class EditConfig extends JFrame implements ActionListener {
    private JPanel panel;
    private JButton save, exit, browseProgramPath, browseStoragePath, browseUSBPath;
    private JTextField programPath, storagePath, usbPath;
    private JFileChooser fileChooser;
    private Configuration config;
    private JFrame gui;

    public EditConfig(Configuration c, JFrame g) {
        config = c;
        gui = g;
        fileChooser = new JFileChooser("./");

        //Setting up JPanel
        panel = new JPanel(new FlowLayout());

        //Setting up components - TextFields
        programPath = new JTextField(20);
        storagePath = new JTextField(20);
        usbPath = new JTextField(20);

        programPath.setText(config.loadConfiguration("programPath"));
        storagePath.setText(config.loadConfiguration("storagePath"));
        usbPath.setText(config.loadConfiguration("usbPath"));

        //Setting up components - Buttons
        save = new JButton("Save");
        exit = new JButton("Exit");
        browseProgramPath = new JButton("Browse...");
        browseStoragePath = new JButton("Browse...");
        browseUSBPath = new JButton("Browse...");

        save.addActionListener(this);
        exit.addActionListener(this);
        browseProgramPath.addActionListener(this);
        browseStoragePath.addActionListener(this);
        browseUSBPath.addActionListener(this);

        //Adding components to panel
        panel.add(new JLabel("Path to Program Folder:"));
        panel.add(programPath);
        panel.add(browseProgramPath);
        panel.add(new JLabel("Path to Images and Scans Folder:"));
        panel.add(storagePath);
        panel.add(browseStoragePath);
        panel.add(new JLabel("Default Path to USB Drive:"));
        panel.add(usbPath);
        panel.add(browseUSBPath);
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
        setSize(450, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed (ActionEvent e){
        if (e.getSource() == save) {
            //Saves configuration based on user input
            config.saveConfiguration("programPath", programPath.getText());
            config.saveConfiguration("storagePath", storagePath.getText());
            config.saveConfiguration("usbPath", usbPath.getText());

            config.saveConfiguration("Initializer", "1"); //this will tell the program that settings have been inputted
            JOptionPane.showMessageDialog(this, "Saved!", "Save Successful", JOptionPane.PLAIN_MESSAGE); //tell user suh
        } else if (e.getSource() == exit) { //close this gui and bring back the other
            gui.setVisible(true);
            gui.setAutoRequestFocus(true);
            dispose();
        } else if (e.getSource() == browseProgramPath) {
            fileChooser.setVisible(true);
            int choice = fileChooser.showOpenDialog(this);
        } else if (e.getSource() == browseStoragePath) {

        } else if (e.getSource() == browseUSBPath) {

        }
    }
}
