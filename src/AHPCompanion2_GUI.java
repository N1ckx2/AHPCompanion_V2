import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */
public class AHPCompanion2_GUI extends JFrame implements ActionListener {
    private JPanel panel;
    private JComboBox apComboBox;
    private JComboBox gComboBox;
    private JComboBox tComboBox;
    private JRadioButton scanOrImgRadio;
    private JTextField sourceText;
    private JTextField destText;
    private JLabel apLabel;
    private JLabel gLabel;
    private JLabel tLabel;
    private JLabel destPathLabel;
    private JLabel sourcePathLabel;
    private JTextField traverse, fit;
    private JComboBox<String> scanOrImg;
    private JButton enter, settings;
    private AHP_Companion_2 main;

    public AHPCompanion2_GUI () {
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel = new JPanel();
        apLabel = new JLabel();
        gLabel = new JLabel();
        apComboBox = new JComboBox();
        gComboBox = new JComboBox();
        tLabel = new JLabel();
        tComboBox = new JComboBox();
        scanOrImgRadio = new JRadioButton();
        sourceText = new JTextField();
        destText = new JTextField();
        sourcePathLabel = new JLabel();
        destPathLabel = new JLabel();
    }
}
