package models;

import objects.youtubeObjects.youtubeVideo;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 1/11/14.
 * @author Loc Do
 */

public class model {

    /**
     * List of videos in the folder
     */
    List<youtubeVideo> lstVideo;

    public model() {
        lstVideo = new LinkedList<youtubeVideo>();
    }

    public void run() {
        createListVideos("youtube");
    }

    /**
     * Read all the json files in the folderName
     * @param folderName
     */
    void createListVideos(String folderName) {
	try {
	    lstVideo = utilities.DatafileGrabber.readListOfVideos(folderName);
	    System.out.println("Finished reading all the videos in folder "+folderName);
	    System.out.println("There are "+lstVideo.size()+" videos.");
	} catch (java.io.IOException e) {
	    e.printStackTrace();
	}
    }
}
