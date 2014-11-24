package controllers.dataControllers;

import objects.youtubeObjects.youtubeUser;
import objects.youtubeObjects.youtubeVideo;
import utilities.Common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import utilities.DatafileGrabber;
/**
 * Create on 23/11/14
 *
 * @author Loc Do
 */
public class videoController {

    /*
     * Main method of the class
     * @param videoFolder : path to the directory contains date folders
     */
    public static void run(String videoFolder) throws java.io.IOException {
        System.out.println("Reading in video data...");
        //List<youtubeVideo> vidList = utilities.DatafileGrabber.readListOfVideos(videoFolder);

        final File folder = new File(videoFolder);
        List<youtubeVideo> list = new java.util.LinkedList<youtubeVideo>();

        long numFailedDateReads = 0;
        long numExtractedVideo = 0;

        for (final File dateFolder : folder.listFiles()) {
            if (dateFolder.isDirectory()) {
                list.clear();
                String outputFilename = dateFolder.getName()+".txt";
                System.out.println("Wrapping data at folder " + dateFolder.getName());

                numFailedDateReads += DatafileGrabber.readListOfVideosFromDayFolder(list, dateFolder);

                // Remove duplicated videos
                HashMap<String, youtubeVideo> map = makeVideoMap(list);
                numExtractedVideo += map.size();

                exportToCSV(map, outputFilename);
            }
        }

        System.out.println("Cannot extract " + numFailedDateReads + " videos over " + numExtractedVideo);

        System.out.println("Done.");
    }

    /* Takes all videos in a list, and uses their data to construct a map of user names to youtubeUsers. */
    static HashMap<String,youtubeVideo> makeVideoMap(List<youtubeVideo> vidList) {
        HashMap<String,youtubeVideo> videoMap = new java.util.HashMap<String, youtubeVideo>();
        for (youtubeVideo vid : vidList) {
            videoMap.put(vid.getKey(), vid);
        }
        return videoMap;
    }

    static void exportToCSV(HashMap<String, youtubeVideo> map, String filename) throws IOException {
        java.io.File outFile = new java.io.File(filename);
        java.io.BufferedWriter bw = null;
        try {
            bw = new java.io.BufferedWriter(new java.io.FileWriter(outFile));
            int failures = 0;
            for (String key: map.keySet()) {
                try {
                    youtubeVideo vid = map.get(key);
                    boolean isValid = true;

                    String newLine = "";
                    String processedStr = "";
                    // VideoID
                    newLine += vid.getKey()+";";

                    // Video Bag of Words
                    String processedTitle = Common.strPreprocessing(vid.getTitle());
                    String[] strArr = processedTitle.split("\\s+");
                    for (String str:strArr)
                        processedStr+=str+",";

                    newLine += processedStr.substring(0, processedStr.length()-1)+";";

                    // Uploader
                    newLine+= vid.getChannelID()+";";

                    // how long since uploaded
                    if (vid.getHowLongAgoUploaded()<=0) isValid = false;
                    newLine+= vid.getHowLongAgoUploaded()+";";

                    // video length in seconds
                    if (vid.getVideoLengthInSeconds()<=0) isValid = false;
                    newLine+= vid.getVideoLengthInSeconds()+";";

                    // viewCount
                    if (vid.getViewCount()<=0) isValid = false;
                    newLine+= vid.getViewCount()+";";

                    // noOfLikes
                    if (vid.getNoOfLikes()<=0) isValid = false;
                    newLine+= vid.getNoOfLikes()+";";

                    // noOfDislikes
                    if (vid.getNoOfDislikes()<=0) isValid = false;
                    newLine+= vid.getNoOfDislikes()+";";

                    // Description
                    String processedDescription = Common.strPreprocessing(vid.getDescription());
                    strArr = processedDescription.split("\\s+");
                    processedStr = "";
                    for (String str:strArr)
                        processedStr+=str+",";

                    newLine += processedStr.substring(0, processedStr.length()-1)+";";

                    // Category
                    if (vid.getCategory().trim().length()>0)
                        newLine += vid.getCategory()+"\n";
                    else
                        newLine += "none"+"\n";

                    if (isValid)
                        bw.write(newLine);

                } catch (Exception e) {
                    e.printStackTrace();
                    failures++;
                }
            }
            bw.close();
            if (failures > 0)
                System.err.println("WARNING: " + failures + " entries of the video map failed to output correctly."
                        +  " You should not trust the output file \"" + filename + "\" at all.");

        } catch (java.io.IOException e) {
            if (bw != null)
                bw.close();
            throw e;
        }
    }
}
