import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */
public class AHPCompanion2_GUI extends JFrame implements ActionListener {
    private JPanel panel;
    private JTextField traverse, fit;
    private JComboBox<String> scanOrImg;
    private JButton enter, settings;
    private AHP_Companion_2 main;

    public AHPCompanion2_GUI () {
        //Configure JPanels
        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        //Set up JComponents
        traverse = new JTextField(4);
        fit = new JTextField(4);
        scanOrImg = new JComboBox<String>();
        scanOrImg.addItem("Scans");
        scanOrImg.addItem("Images");

        //Set up Buttons
        enter = new JButton("Enter");
        settings = new JButton("Settings");
        enter.addActionListener(this);
        settings.addActionListener(this);

        //Add Necessary Components to JPanel
        panel.add(new JLabel("Traverse:"));
        panel.add(traverse);
        panel.add(new JLabel("Fit:"));
        panel.add(fit);
        panel.add(scanOrImg);
        panel.add(enter);
        panel.add(settings);

        //Configure JFrame
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //makes the buttons look and feel like windows
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("AHP Companion 2");
        setContentPane(panel);
        pack ();
        setResizable(false);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null); // Center window
        setVisible(true);

        main = new AHP_Companion_2(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (main.checkConfig()) {
            if (e.getActionCommand().equals("Enter")) {
                //Will be used to check if move worked
                boolean success = false;
                if (scanOrImg.getSelectedItem().equals("Scans")) {
                    success = main.moveScans(Integer.parseInt(traverse.getText()), Integer.parseInt(fit.getText()));
                } else if (scanOrImg.getSelectedItem().equals("Images")) {
                    success = main.moveImages(Integer.parseInt(traverse.getText()), Integer.parseInt(fit.getText()));
                }

                //If it worked, tell user. If it didn't work, an error dialogue should have popped up.
                if (success) {
                    JOptionPane.showMessageDialog(this, "Moved!", "Move Successful", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (e.getActionCommand().equals("Settings")) {
                setVisible(false);
                new EditConfig(main.getConfig(), this); //opens up edit configuartion
            }
        }
    }

    public static void main (String[] args){
        new AHPCompanion2_GUI();
    }
}
