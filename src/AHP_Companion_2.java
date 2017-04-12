import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

/**
 * Created by Nicholas Vadivelu on 2016-07-26.
 */
public class AHP_Companion_2 {
    private String programPath, storagePath, usbPath;
    private int expedition;
    private Configuration config; //XML file used to store path data
    private AHPCompanion2_GUI gui;

    final private int[][] NUM_STATIONS = { //[fit][traverse], leading zeros so indexing isn't weird
            {0, 0, 0, 0}, //empty
            {0, 1, 5, 4}, //FIT 1: 10 SS
            {0, 1, 4, 8}, //FIT 2: 13 SS
            {0, 1, 6, 6}, //FIT 3: 13 SS
            {0, 1, 5, 5}, //FIT 4: 14 SS
            {0, 1, 7, 4}, //FIT 5: 12 SS
            {0, 1, 8, 5}, //FIT 6: 14 SS
    };

    public AHP_Companion_2(AHPCompanion2_GUI g) {
        gui = g; //local version of gui to manipulate
        config = new Configuration("AHP_Companion_2", "config.xml"); //configuration file to get data
        checkConfig(); //checks if it exists, and if not, makes user enter required data
        expedition = Calendar.getInstance().get(Calendar.YEAR) - 1999; //AP number based on the current year
    }

    public boolean moveScans(int traverse, int fit) {
        //Establishes source nad destination paths
        String ssrfPath = storagePath + "\\Scans\\FIT " + fit;
        String destPath = programPath + "\\AHPDATA\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse + "\\SS";

        File[] scans = new File(ssrfPath).listFiles(); //retrieves files

        //Make sure the number of files is correct, and if not don't proceed
        if (scans.length/4 != NUM_STATIONS[fit][traverse]) {
            JOptionPane.showMessageDialog(gui, "" +scans.length/4 + " sample stations found, but there should be " + NUM_STATIONS[fit][traverse], "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        //Moves files into appropriate folders based on GTS
        for (int i = 1 ; i <= scans.length/4 ; i++) {
            copyFile(scans[(i-1)*4], destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"wd.jpg");
            copyFile(scans[(i-1)*4+1],   destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"sm.jpg");
            copyFile(scans[(i-1)*4+2], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"1.jpg");
            copyFile(scans[(i-1)*4+3], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"2.jpg"); //add double slash in maybe
        }

        return true;
    }


    public boolean moveImages(int traverse, int fit) {
        //Establishes source nad destination paths
        String imagePath = storagePath + "\\Images\\FIT " + fit;
        String destPath = programPath + "\\AHPDATA\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse + "\\SS";

        File[] images = new File(imagePath).listFiles();//retrieves files

        //Counts the number of black images in the folder (indicates the end of images from one SS)
        int numBlack = 0;
        for (int i = 0 ; i < images.length ; i++){
            if (images[i].toString().substring(images[i].toString().length()-5).toUpperCase().equals("B.JPG")){
                numBlack++;
            }
        }

        //If the number of black images doesn't match what was expected, tell user
        if (numBlack != NUM_STATIONS[fit][traverse]) {
            JOptionPane.showMessageDialog(gui, "" + numBlack + " black images found, but there should be " + NUM_STATIONS[fit][traverse], "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        //Move files to appropriate locations, using the black images as markers
        for (int i = 1, j = 0; i <= numBlack && j < images.length ; i++, j++) {
            while (!images[j].toString().substring(images[j].toString().length()-5).toUpperCase().equals("B.JPG")){
                copyFile(images[j], destPath + i + "\\Images\\RAW", destPath + i + "\\Images\\RAW\\" + images[j].getName());
                j++;
            }
        }

        return true;
    }

    private static void copyFile(File source, String destPath, String name){ //Moves files from source to destPath
        Path src = Paths.get(source.toString()); //original file
        Path targetDir = Paths.get(destPath.toString());
        try {
            Files.createDirectories(targetDir); //in case target directory didn't exist
            Path target = targetDir.resolve(name); //create new path with destName
            Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConfig () {
        try {
            programPath = config.loadConfiguration("programPath");
            storagePath = config.loadConfiguration("storagePath");
            usbPath = config.loadConfiguration("usbPath"); //tries to retrive values from the config.xml for these paths
            if (programPath.equals("") || storagePath.equals("") || usbPath.equals("")) { //if any of these are blank, prompt user with EditConfig
                gui.setVisible(false);
                new EditConfig(config, gui);
                return false;
            }
        } catch (NullPointerException e) { //inevitable, if the xml doesn't exist, a Nullpointer will be thrown, in which case do the same thing in the if statement
            gui.setVisible(false);
            new EditConfig(config, gui);
            return false;
        }
        return true;
    }

    public Configuration getConfig() { return config; }
}
