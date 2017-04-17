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
        int[] ss = getSSNums(traverse, fit);
        return moveScans(traverse, fit, ss[0], ss[ss.length-1]);
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
        for (int i = ssi ; i <= ssf ; i++) {
            //into storage folder
            copyFile(scans[(i-ssi-1)*4], ssrfPath, "" + expedition+traverse+fit+(i)+"wd.jpg");
            copyFile(scans[(i-ssi-1)*4+1],   ssrfPath, "" + expedition+traverse+fit+(i)+"sm.jpg");
            copyFile(scans[(i-ssi-1)*4+2], ssrfPath,      "" + expedition+traverse+fit+(i)+"1.jpg");
            copyFile(scans[(i-ssi-1)*4+3], ssrfPath,      "" + expedition+traverse+fit+(i)+"2.jpg"); //add double slash in maybe

            //into AHP data
            copyFile(scans[(i-ssi-1)*4], destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"wd.jpg");
            copyFile(scans[(i-ssi-1)*4+1],   destPath + i + "\\Scans\\Drawings", "" + expedition+traverse+fit+(i)+"sm.jpg");
            copyFile(scans[(i-ssi-1)*4+2], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"1.jpg");
            copyFile(scans[(i-ssi-1)*4+3], destPath + i + "\\Scans\\FDS",      "" + expedition+traverse+fit+(i)+"2.jpg"); //add double slash in maybe
        }

        return true;
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

        int[] ssNums = new int[scans.length];
        for (int i = 0 ; i < scans.length ; i++){
            ssNums[i] = scans[i].toString().charAt(scans[i].toString().length()-1)-'0';
        }

        return ssNums;
    }

    public boolean moveImages(int traverse, int fit) {
        int[] ss = getSSNums(fit, traverse);
        return moveImages(traverse, fit, ss[0], ss[ss.length-1]);
    }

    public boolean moveImages(int traverse, int fit, int ssi, int ssf) {
        //Establishes source nad destination path
        String source = sdPath + "\\DCIM";
        String imagePath = storagePath + "\\Images\\FIT" + fit;
        String destPath = ahpdataPath + "\\AHPDATA\\AP" + expedition + "\\FIT"+ fit + "\\T" + traverse + "\\SS";

        File[] images = new File(source).listFiles(); //retrieves files
        int[] blackimgs = getBlack(images);

        //move photos to storage directory
        for (int i = 0 ; i < images.length ; i++){
            copyFile(images[i], imagePath, images[i].toString());
        }

        //move images to ahp data
        for (int i = 0 ; i < blackimgs.length-1; i++ ) {
            for (int j = blackimgs[i] ; j < blackimgs[i+1] ; i++) {
                copyFile(images[j], destPath + ssi++, images[j].toString());
            }
        }
        for (int j = blackimgs[blackimgs.length-1] ; j < images.length ; j++) {
            copyFile(images[j], destPath + ssi, images[j].toString());
        }
        if (ssi != ssf) { //checks to make sure the desired sample stations were covered
            JOptionPane.showMessageDialog(gui, "Error moving photos", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private int[] getBlack (File[] images) {
        LinkedList<Integer> inds = new LinkedList<>();
        inds.add(0);

        for (int i = 0 ; i < images.length ; i++) {
            BufferedImage img = null;
            double avg = 0; //average rgb value

            try {
                img = ImageIO.read(images[i]); //reads in the image
                for (int j = 0 ; j < img.getHeight(); j++) { //goes through all the pixels
                    for (int k = 0 ; k < img.getWidth() ; k ++) {
                        avg += img.getRGB(k, j); //summs of rgb values
                    }
                }
                avg /= img.getHeight()*img.getWidth(); //finds average rgb value
            } catch (IOException e) {
                avg = Integer.MAX_VALUE; //if image loading failed, set aveage to MAX;
            }

            if (avg < 10)
                inds.add(i); //adds image if it's black
        }

        int[] imgs = new int[inds.size()];
        int count = 0;
        for (int i : inds) {
            imgs[count++] = i;
        }
        return imgs;
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
            ahpdataPath = config.loadConfiguration("ahpdataPath");
            storagePath = config.loadConfiguration("sourcePath");
            usbPath = config.loadConfiguration("usbPath");
            sdPath = config.loadConfiguration("sdPath"); //tries to retrive values from the config.xml for these paths
            if (ahpdataPath.equals("") || storagePath.equals("") || usbPath.equals("") || sdPath.equals("")) { //if any of these are blank, prompt user with EditConfig
                System.out.println("false");
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
