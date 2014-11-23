package models;

import objects.youtubeObjects.youtubeVideo;
import utilities.Common;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Loc Do on 1/11/14.
 */
public class regressionModel extends model {

    Set<String> dict;
    /**
     * Constructor
     */
    public regressionModel() {
        super.run();
        dict = new HashSet<String>();
    }

    /**
     * Main method of the class
     */
    public void run() {

        createDictionary();

        System.out.println("There are "+dict.size()+" words in the dictionary.");

        //for (youtubeVideo videoObj:lstVideo)
        //    System.out.println(videoObj.getTitle());
    }

    /**
     *
     * @return true
     *              If no errors occur during the running stage
     *
     */
    boolean createDictionary() {
        try {
            for (youtubeVideo video : lstVideo) {
                //System.out.println(video.getTitle()+" --> "+Common.strPreprocessing(video.getTitle()));
                //System.out.println();
                String title = Common.strPreprocessing(video.getTitle());
                String[] strArr = title.split("\\s+");
                for (String str : strArr)
                    dict.add(str);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
