import sun.util.calendar.LocalGregorianCalendar;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by Nicholas Vadivelu on 2016-07-27.
 */

public class Configuration { //this class allows the program to remember user settings
    private Properties properties = new Properties(); //allows program to understand XML
    private String path; //that path to the XML file
    private String programName;

    public Configuration (String prgrmName, String pathName){
        path = pathName;
        programName = prgrmName;
    }

    public Configuration () {
        new Configuration("Generic Program", "config.xml");
    }

    public void saveConfiguration(String key, String val) { //allows the program to store a property in the XML
        try {
            File file = new File(path); //creates file
            if (!file.exists()) { //checks if the file exists, and if not, create a new one with default properties
                file.createNewFile();
                saveConfiguration("Initializer", "0");
            }
            OutputStream write = new FileOutputStream(path);
            properties.setProperty(key, val); //encodes a property with a key for later access
            properties.storeToXML(write, programName + "Configuration File"); //titles the XML
            write.close(); //closes stream

        } catch (Exception e) { //tells user if there is an error in the configuration
            JOptionPane.showMessageDialog(null, "Error in Configuration.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String loadConfiguration(String key) { //allows the program to load a configuration
        String prop = "";
        try {
            InputStream read = new FileInputStream(path); //reading file
            properties.loadFromXML(read); //loading properties
            prop = properties.getProperty(key); //getting property
            read.close(); //closing stream
        } catch (FileNotFoundException e) { //just in case there is no configuration yet, make a new one
            saveConfiguration("Initializer", "0");
            loadConfiguration(key); //then load configuration
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error in loading configuration!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return prop; //returns the requested property
    }
}
