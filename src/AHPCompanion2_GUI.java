import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */
public class AHPCompanion2_GUI extends JFrame implements ActionListener {
    private JPanel panel;
    private JTextField sourceText, destText, storagePathText;
    private JLabel apLabel, gLabel, tLabel, destPathLabel, sourcePathLabel;
    private JSpinner apSpinner, gSpinner, tSpinner;
    private JLabel storagePathLabel;
    private JTextField traverse, fit;
    private JComboBox<String> scanOrImg;
    private JButton moveBtn, settingsBtn;
    private AHP_Companion_2 main;
    private Configuration config;

    public AHPCompanion2_GUI () {
        //Configure JFrame
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //makes the buttons look and feel like windows
        } catch (Exception e) {
            e.printStackTrace();
        }
        main = new AHP_Companion_2(this);
        config = main.getConfig();
        //setting up components
        int exp = Calendar.getInstance().get(Calendar.YEAR) - 1999; //expedition number
        apSpinner.setModel(new SpinnerNumberModel(exp, 8, exp, 1));
        gSpinner.setModel(new SpinnerNumberModel(1, 1, 6, 1));
        tSpinner.setModel(new SpinnerNumberModel(0, 0, 3, 1));
        scanOrImg.addItem("Scans");
        scanOrImg.addItem("Images");

        //fill text fields with destinations
        sourceText.setText(config.loadConfiguration("sourcePath") + "\\FIT 1");
        destText.setText(config.loadConfiguration("ahpdataPath") + "\\AP" + apSpinner.getValue() + "\\FIT"+ gSpinner.getValue() + "\\T" + tSpinner.getValue() + "\\SS1\\Scans");
        storagePathText.setText(config.loadConfiguration("usbPath") + "FIT 1");

        //set up action listener
        settingsBtn.addActionListener(this);
        moveBtn.addActionListener(this);

        setTitle("AHP Companion 2");
        setContentPane(panel);
        pack ();
        setResizable(true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null); // Center window
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (main.checkConfig()) {
            if (e.getSource() == moveBtn) {
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
        } else if (e.getSource() == settingsBtn) {
            setVisible(false);
            new EditConfig(main.getConfig(), this); //opens up edit configuartion
        }
    }

    public static void main (String[] args){
        new AHPCompanion2_GUI();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); //gives the layout some padding

        apLabel = new JLabel();
        gLabel = new JLabel();
        sourcePathLabel = new JLabel();
        destPathLabel = new JLabel();

        //spinner set up
        apSpinner = new JSpinner();
        gSpinner = new JSpinner();
        tSpinner = new JSpinner();

        tLabel = new JLabel();
        sourceText = new JTextField(30);
        destText = new JTextField(30);
        storagePathText = new JTextField(30);

        moveBtn = new JButton();
        settingsBtn = new JButton();
    }
}
