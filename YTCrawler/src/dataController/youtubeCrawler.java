package dataController;

/**
 * Created by larcuser on 2/10/14.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.*;
import dataObject.*;
import utility.Configuration;

public class youtubeCrawler {

    public void run(String seedURL) {
        try {
            Queue<String> vIDQueue = new LinkedList<String>();
            vIDQueue.add(seedURL);
            long counter = 1;

            while (!vIDQueue.isEmpty()) {
                String newVideoID = vIDQueue.remove();
                Configuration.getInstance().getDict().add(newVideoID);
                youtubeVideo videoObj = youtubeExtractor.extract(newVideoID);
                if (counter % 10000 ==0) {
                    System.out.println("Downloaded " + counter + " videos...");
                    Thread.sleep(1000*Configuration.getInstance().getDelayTimeInSeconds());
                }

                if (videoObj != null) {
                    for (String shortUrl : videoObj.getRelatedURL())
                        if (!Configuration.getInstance().getDict().contains(shortUrl))
                            vIDQueue.add(shortUrl.substring(9));

                    dataController.saveToDisk(videoObj.getKey(), videoObj.exportDataToJSON());
                    counter++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error at dataController/youtubeCrawler: " + e.getMessage());
        }
    }
}
