package dataController;

import java.io.*;
import org.joda.time.DateTime;

/**
 * Created by larcuser on 3/10/14.
 */
public class dataController {
    public static void saveToDisk(String key, String content) {
        try {
            DateTime dt = new DateTime();
            String folderName = String.valueOf(dt.getYear()) + dt.getMonthOfYear() + dt.getDayOfWeek();
            File folder = new File(folderName);
            if (!folder.exists())
                if (!folder.mkdir())
                    throw new Exception("Cannot create folder "+folderName);

            String fileName = folderName+"/"+key+".json";
            File newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();

                FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }
        } catch (Exception e) {
            System.out.println("Error at dataController/dataController: "+e.getMessage());
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
