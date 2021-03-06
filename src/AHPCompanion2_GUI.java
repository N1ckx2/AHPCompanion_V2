import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
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
    private JButton moveBtn, settingsBtn, backupButton;
    private AHP_Companion_2 main;
    private JFileChooser fileChooser;
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

        fileChooser = new JFileChooser("./");
        fileChooser.setMultiSelectionEnabled(true); //allows selection of multiple images
        ImagePreviewPanel preview = new ImagePreviewPanel();
        fileChooser.setAccessory(preview);
        fileChooser.addPropertyChangeListener(preview);

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
        backupButton.addActionListener(this);

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
                        success = main.moveScans((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), Integer.parseInt((String)ssiCombo.getSelectedItem()));
                    else //both positions defined
                        success = main.moveScans((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), Integer.parseInt(ssiCombo.getSelectedItem().toString()), Integer.parseInt(ssfCombo.getSelectedItem().toString()));
                }

                //Moving Images
                else if (scanOrImg.getSelectedItem().equals("Images")) { //opens a file chooser and allows user to select images on sample station at a time
                    fileChooser.setCurrentDirectory(new File(storagePathText.getText()));
                    int[] ss = getSS((Integer)tSpinner.getValue(), (Integer) gSpinner.getValue());
                    String directory = "";
                    success = true;
                    for (int i = 0 ; i < ss.length; i++) {
                        fileChooser.setDialogTitle("Sample Station " + ss[i]);
                        int returnVal = fileChooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION){
                            success = main.moveImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), ss[i], fileChooser.getSelectedFiles());
                            directory = fileChooser.getCurrentDirectory().toString();
                        }
                    }

                    //backs up all the transferred images
                    if (!directory.equals(""))
                        success = main.backUpImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), directory);
                }



            } else if (e.getSource() == backupButton) {
                boolean success = true;
                //Moving scans
                if (scanOrImg.getSelectedItem().equals("Scans")) {
                    if (ssiCombo.getSelectedItem().equals("Auto")) //completely automatic
                        success = main.usbScansToBackup((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue());
                    else if (ssfCombo.getSelectedItem().equals("Auto")) //defined start position
                        success = main.usbScansToBackup((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), (Integer) ssiCombo.getSelectedItem());
                    else //both positions defined
                        success = main.usbScansToBackup((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), Integer.parseInt(ssiCombo.getSelectedItem().toString()), Integer.parseInt(ssfCombo.getSelectedItem().toString()));
                } else if (scanOrImg.getSelectedItem().equals("Images")) {
                    fileChooser.setCurrentDirectory(new File(storagePathText.getText())); //create filechooser
                    String directory = ""; //initialize directory

                    fileChooser.setDialogTitle("Images Directory");
                    int returnVal = fileChooser.showOpenDialog(this); //prompt user to choose directory
                    if (returnVal == JFileChooser.APPROVE_OPTION)
                        directory = fileChooser.getCurrentDirectory().toString();

                    if (!directory.equals("")) //backup images if approved
                        success = main.backUpImages((Integer) tSpinner.getValue(), (Integer) gSpinner.getValue(), directory);
                }
                //If it worked, tell user. If it didn't work, an error dialogue should have popped up.
                if (success)
                    JOptionPane.showMessageDialog(this, "Moved!", "Move Successful", JOptionPane.INFORMATION_MESSAGE);


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

    public int[] getSS (int traverse, int fit) {
        int[] ss = main.getSSNums(traverse, fit);

        if (!ssiCombo.getSelectedItem().equals("Auto")) { //defined start position
            int start = Integer.parseInt(ssiCombo.getSelectedItem().toString()); //get start position
            String selectedFin = ssfCombo.getSelectedItem().toString(); //get ssf text
            int finish = (selectedFin.equals("Auto") ? ss[ss.length-1] : Integer.parseInt(selectedFin)); //if it's defined, set it at that, if not, set to end

            ArrayList<Integer> nums = new ArrayList<Integer>();
            for (int i : ss){
                if (i >= start && i <= finish) nums.add(i);
            }
            ss = new int[nums.size()];
            for (int i = 0 ; i < ss.length ; i++) {
                ss[i] = nums.get(i);
            }
        }

        return ss;
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
        for (int i = 0 ; i < ss.length; i++){
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
