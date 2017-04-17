import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */

public class AHPCompanion2_GUI extends JFrame implements ActionListener, ChangeListener {
    private JPanel panel;
    private JTextField sourceText, destText, storagePathText;
    private JLabel apLabel, gLabel, tLabel, destPathLabel, sourcePathLabel, storagePathLabel;
    private JSpinner apSpinner, gSpinner, tSpinner;
    private JComboBox<String> scanOrImg, ssiCombo, ssfCombo;
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

        //setting up spinners
        int exp = Calendar.getInstance().get(Calendar.YEAR) - 1999; //expedition number
        apSpinner.setModel(new SpinnerNumberModel(exp, 8, exp, 1));
        gSpinner.setModel(new SpinnerNumberModel(1, 1, 6, 1));
        tSpinner.setModel(new SpinnerNumberModel(0, 0, 3, 1));
        apSpinner.addChangeListener(this);
        gSpinner.addChangeListener(this);
        tSpinner.addChangeListener(this);

        //setting up comboboxes
        scanOrImg.addItem("Scans");
        scanOrImg.addItem("Images");
        scanOrImg.addActionListener(this);
        ssiCombo.addItem("Auto");
        ssfCombo.addItem("Auto");

        //fill text fields with destinations
        updateComponents();

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

                //Moving scans
                if (scanOrImg.getSelectedItem().equals("Scans")) {
                    if (ssiCombo.getSelectedItem().equals("Auto")) //completely automatic
                        success = main.moveScans((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue());
                    else if (ssfCombo.getSelectedItem().equals("Auto")) //defined start position
                        success = main.moveScans((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), (Integer) ssiCombo.getSelectedItem());
                    else //both positions defined
                        success = main.moveScans((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), (Integer) ssiCombo.getSelectedItem(), (Integer) ssfCombo.getSelectedItem());
                }

                //Moving Images
                else if (scanOrImg.getSelectedItem().equals("Images")) {
                    if (ssiCombo.getSelectedItem().equals("Auto")) //completely automatic
                        success = main.moveImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue());
                    else if (ssfCombo.getSelectedItem().equals("Auto")) //defined start position
                        success = main.moveImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), (Integer) ssiCombo.getSelectedItem());
                    else //both positions defined
                        success = main.moveImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), (Integer) ssiCombo.getSelectedItem(), (Integer) ssfCombo.getSelectedItem());
                }

                //If it worked, tell user. If it didn't work, an error dialogue should have popped up.
                if (success) {
                    JOptionPane.showMessageDialog(this, "Moved!", "Move Successful", JOptionPane.INFORMATION_MESSAGE);
                }

            } else if (e.getSource() == settingsBtn) {
                setVisible(false);
                new EditConfig(main.getConfig(), this); //opens up edit configuartion
            }
        } else if (e.getSource() == settingsBtn) {
            setVisible(false);
            new EditConfig(main.getConfig(), this); //opens up edit configuartion
        }
        updateComponents();
    }

    public void stateChanged (ChangeEvent e) {
        updateComponents();
    }

    public void updateComponents() {
        if (scanOrImg.getSelectedItem().equals("Scans")) {
            sourceText.setText(config.loadConfiguration("sourcePath") + "\\Scans\\FIT" + gSpinner.getValue());
            destText.setText(config.loadConfiguration("ahpdataPath") + "\\AP" + apSpinner.getValue() + "\\FIT" + gSpinner.getValue() + "\\T" + tSpinner.getValue() + "\\SS1\\Scans");
            storagePathText.setText(config.loadConfiguration("usbPath") + "FIT"+gSpinner.getValue());
        } else if (scanOrImg.getSelectedItem().equals("Images")) {
            sourceText.setText(config.loadConfiguration("sourcePath") + "\\Images\\FIT" + gSpinner.getValue());
            destText.setText(config.loadConfiguration("ahpdataPath") + "\\AP" + apSpinner.getValue() + "\\FIT" + gSpinner.getValue() + "\\T" + tSpinner.getValue() + "\\SS1\\Images");
            storagePathText.setText(config.loadConfiguration("sdPath") + "DCIM");
        }

        int[] ss = main.getSSNums((Integer)tSpinner.getValue(), (Integer) gSpinner.getValue());
        ssiCombo.removeAllItems();
        ssfCombo.removeAllItems();
        ssiCombo.addItem("Auto");
        ssfCombo.addItem("Auto");
        for (int i = 0 ; i < ss.length ; i++){
            ssiCombo.addItem(""+ss[i]);
            ssfCombo.addItem(""+ss[i]);
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
