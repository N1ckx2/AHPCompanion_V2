import com.sun.javafx.image.IntPixelGetter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by Nicholas Vadivelu on 2016-07-26.
 */
public class AHP_Companion_2 {
    private String ahpdataPath, storagePath, usbPath, sdPath;
    private int expedition;
    private Configuration config; //XML file used to store path data
    private AHPCompanion2_GUI gui;

    public AHP_Companion_2(AHPCompanion2_GUI g) {
        gui = g; //local version of gui to manipulate
        config = new Configuration("AHP_Companion_2", "config.xml"); //configuration file to get data
        checkConfig(); //checks if it exists, and if not, makes user enter required data
        expedition = Calendar.getInstance().get(Calendar.YEAR) - 1999; //AP number based on the current year
    }

    public int[] getSSNums (int traverse, int fit) { //get number of sample stations
        String path = ahpdataPath + "\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse;

        File file = new File(path);
        File[] scans = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });

        int[] ssNums = new int[scans.length-1];
        for (int i = 1 ; i < scans.length; i++){ //starts at 1 to avoid the data folder
            ssNums[i-1] = scans[i].toString().charAt(scans[i].toString().length()-1)-'0';
        }

        return ssNums;
    }

    public boolean moveScans(int traverse, int fit) {
        int[] ss = getSSNums(traverse, fit);
        return moveScans(traverse, fit, ss[0], ss[ss.length-1]);
    }

    public boolean moveScans(int traverse, int fit, int ssi) {
        int[] ss = getSSNums(traverse, fit);
        return moveScans(traverse, fit, ssi, ss[ss.length-1]);
    }

    public boolean moveScans(int traverse, int fit, int ssi, int ssf) {
        //Establishes source nad destination paths
        String ssrfPath = storagePath + "\\Scans\\T"+traverse+"\\FIT" + fit;
        String destPath = ahpdataPath + "\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse + "\\SS";
        String USBsource = usbPath + "\\FIT" + fit;

        File[] scans = new File(USBsource).listFiles(); //retrieves files

        //Make sure the number of files is correct, and if not don't proceed
        /*
        if (scans.length/4 != NUM_STATIONS[fit][traverse]) {
            JOptionPane.showMessageDialog(gui, "" +scans.length/4 + " sample stations found, but there should be " + getSSNums(fit, traverse).length, "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }*/

        //Moves files into appropriate folders based on GTS
        int counter = 0; //to iterate through properly
        for (int i = ssi ; i <= ssf ; i++) {
            //into storage folder
            copyFile(scans[counter], ssrfPath, "" + expedition+traverse+fit+(i)+"wd.jpg");
            copyFile(scans[counter+1],   ssrfPath, "" + expedition+traverse+fit+(i)+"sm.jpg");
            copyFile(scans[counter+2], ssrfPath,      "" + expedition+traverse+fit+(i)+"1.jpg");
            copyFile(scans[counter+3], ssrfPath,      "" + expedition+traverse+fit+(i)+"2.jpg"); //add double slash in maybe

            //into AHP data
            copyFile(scans[counter++], destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"wd.jpg");
            copyFile(scans[counter++],   destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"sm.jpg");
            copyFile(scans[counter++], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"1.jpg");
            copyFile(scans[counter++], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"2.jpg"); //add double slash in maybe
        }

        return true;
    }

    public boolean moveImages (int traverse, int fit, int ss, File[] images) {
        String destPath = ahpdataPath + "\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse + "\\SS" + ss + "\\Images\\RAW";
        for (File f : images)
            copyFile(f, destPath, f.getName());
        return true;
    }

    public boolean backUp (int traverse, int fit, String directory) {
        //Backs up files
        String imagePath = storagePath + "\\Images\\T" + traverse + "\\FIT" + fit;

        File[] images = new File(directory).listFiles(); //retrieves all files in that folder
        //move photos to storage directory
        for (int i = 0 ; i < images.length ; i++){
            copyFile(images[i], imagePath, images[i].getName());
        }
        return true;
    }

    private static void copyFile(File source, String destPath, String name){ //Moves files from source to destPath
        //System.out.println(destPath + " " + name);

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
            ahpdataPath = config.loadConfiguration("ahpdataPath");
            storagePath = config.loadConfiguration("sourcePath");
            usbPath = config.loadConfiguration("usbPath");
            sdPath = config.loadConfiguration("sdPath"); //tries to retrive values from the config.xml for these paths
            if (ahpdataPath.equals("") || storagePath.equals("") || usbPath.equals("") || sdPath.equals("")) { //if any of these are blank, prompt user with EditConfig
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
    public int getExpedition() {return expedition;}
}
