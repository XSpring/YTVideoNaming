package controllers.dataControllers;

import java.io.*;
//import java.util.Calendar;

/**
 * Created by larcuser on 3/10/14.
 */
public class dataController {

    public static void saveToDisk(String key, String content, String fileExt) {
        try {
            String fileName = key + fileExt;

            File newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();

                FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }
        } catch (Exception e) {
            //System.out.println("Error at dataController/dataController: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean update(String key, String content) {
        // Not Implement Yet
        return false;
    }

    public static boolean check(String key) {
        // Not Implement Yet
        return false;
    }

    public static boolean checkAndUpdate(String key) {
        // Not Implement Yet
        return false;
    }
}