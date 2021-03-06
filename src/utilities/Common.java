package utilities;

/**
 * Created by larcuser on 1/11/14.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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

    public static String convertDateTimeIndoToEn(String original) {
        original = original.replaceAll("Agt", "Aug");
        original = original.replaceAll("Des", "Dec");
        original = original.replaceAll("Okt", "Oct");
        original = original.replaceAll("Mei", "May");
        return original;
    }

    public static class Pair<F, S> {
	public F first;
	public S second;
	public Pair() {
	    first = null; second = null;
	}
	public Pair(F f, S s) {
	    first = f; second = s;
	}
    }
    public static <E> Pair<List<E>,List<E>> splitToTestAndTraining(List<E> fullList, int sizeOfFirst, java.util.Random rand) {
	if (sizeOfFirst < 0)
	    sizeOfFirst = 0;
	if (sizeOfFirst > fullList.size())
	    sizeOfFirst = fullList.size();
	Collections.shuffle(fullList, rand);
	Pair<List<E>,List<E>> pair;
	pair = new Common.Pair<List<E>,List<E>>();
	pair.first = fullList.subList(0, sizeOfFirst);
	pair.second = fullList.subList(sizeOfFirst, fullList.size());
	return pair;
    }
    
    public static int getSecondsLong(String lengthAsString) {
	System.err.println("Common.getSecondsLong() not yet finished.  Input: "  + lengthAsString);
	return 1;
    }
}
