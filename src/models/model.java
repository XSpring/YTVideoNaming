package models;

import objects.youtubeObjects.youtubeVideo;
import utilities.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by larcuser on 1/11/14.
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
        final File folder = new File(folderName);
        listFilesForFolder(folder);

        System.out.println("Finished reading all the videos in folder "+folderName);
        System.out.println("There are "+lstVideo.size()+" videos.");
    }

    /**
     * If folder is a folder, read all the files in that folder
     * If folder is a JSON file, create a video from that json and put to the lstVideo
     * @param folder
     */
    void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            }
            else
            if (fileEntry.getName().indexOf(".json")>-1) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));

                    String content = "", sCurrentLine = "";
                    while ((sCurrentLine = br.readLine()) != null) {
                        content+=sCurrentLine+"\n";
                    }

                    youtubeVideo videoObj = Common.getInstance().getYTVideoObjectFromJSON(content);
                    if (videoObj!=null)
                        lstVideo.add(videoObj);

                    //System.out.println(fileEntry.getName()+" "+videoObj.getTitle()+" "+videoObj.getKey());
                    /*
                    System.out.println(videoObj.getTitle()+" "+videoObj.getNoOfLikes()
                            +" "+videoObj.getNoOfDislikes()
                            +" "+videoObj.getViewCount());
                    */
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
