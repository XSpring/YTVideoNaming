package utilities;

/**
 * Created by larcuser on 1/11/14.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import objects.youtubeObjects.*;

public class Common {
    private static Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    public void readQueuedIDsFromFile(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";

            Configuration.getInstance().getQueuedIDs().clear();
            if (file.exists())
                while ((line = bufferedReader.readLine()) != null) {
                    Configuration.getInstance().getQueuedIDs().add(line);
                }
            else
                throw new IOException("Cannot open file "+filePath +" for reading data.");

            if (Configuration.getInstance().getQueuedIDs().size()<=0)
                throw new Exception("CrawledIDs is empty!!");

            fileReader.close();

            System.out.println("Finished loading data to the QueuedID queue.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void readCrawledIDsFromFile(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            synchronized (Configuration.getInstance().getCrawledIDs()) {
                Configuration.getInstance().getCrawledIDs().clear();
                if (file.exists())
                    while ((line = bufferedReader.readLine()) != null) {
                        Configuration.getInstance().getCrawledIDs().add(line);
                    }
                else
                    throw new IOException("Cannot open file " + filePath + " for reading data.");

                if (Configuration.getInstance().getCrawledIDs().size()<=0)
                    throw new Exception("CrawledIDs is empty!!");
            }

            fileReader.close();

            System.out.println("Finished loading data to the CrawledID queue.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public youtubeVideo getYTVideoObjectFromJSON(String content) {
        try {
            return Configuration.getInstance().getGson().fromJson(content, youtubeVideo.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param strRaw
     *                  original string
     * @return strProcessed
     *                  processed String
     */
    public static String strPreprocessing(String strRaw) {
        return strRaw.replaceAll("[^0-9\\p{L}.]"," ").toLowerCase();
    }
}
