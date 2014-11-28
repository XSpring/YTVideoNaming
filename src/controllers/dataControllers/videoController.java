package controllers.dataControllers;

import objects.youtubeObjects.youtubeUser;
import objects.youtubeObjects.youtubeVideo;
import utilities.Common;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utilities.DatafileGrabber;
/**
 * Create on 23/11/14
 *
 * @author Loc Do
 */
public class videoController {

    /*
     * Main method of the class
     * @param videoFolder : path to the directory contains data
     */
    public static void run(String videoFolder) throws java.io.IOException {
        computeStatistics(videoFolder);
        //readAndExportToCSV(videoFolder);

    }

    static void computeStatistics(String videoFolder) throws IOException {
        System.out.println("Reading in video data...");
        //List<youtubeVideo> vidList = utilities.DatafileGrabber.readListOfVideos(videoFolder);

        final File folder = new File(videoFolder);

        HashMap<String, youtubeVideo> hmVideo = new HashMap<String, youtubeVideo>();

        HashSet<String> hsChannel = new HashSet<String>();

        for (final File file : folder.listFiles()) {
            String outputFilename = file.getName()+".txt";
            System.out.println("Wrapping data at file " + file.getName());

            BufferedReader br = new BufferedReader(new FileReader(file));
            for(String line; (line = br.readLine()) != null; ) {
                String[] fields = line.split(";");

                String vID = fields[0];
                youtubeVideo newVideo = new youtubeVideo(vID);

                String title = fields[1];
                newVideo.setTitle(title);

                String uploaderID = fields[2];
                newVideo.setChannelID(uploaderID);
                hsChannel.add(uploaderID);

                String strHowLongUploaded = fields[3];
                newVideo.setHowLongAgoUploaded(Long.parseLong(strHowLongUploaded));

                String strLenInSeconds = fields[4];
                newVideo.setVideoLengthInSeconds(Long.parseLong(strLenInSeconds));

                String strViewCount = fields[5];
                newVideo.setViewCount(Long.parseLong(strViewCount));

                String strNoOfLikes = fields[6];
                newVideo.setNoOfLikes(Integer.parseInt(strNoOfLikes));

                String strNoOfDislikes = fields[7];
                newVideo.setNoOfDislikes(Integer.parseInt(strNoOfDislikes));

                String description = fields[8];
                newVideo.setDescription(description);

                String category = fields[9];
                newVideo.setCategory(category);

                hmVideo.put(vID, newVideo);
            }
            br.close();
        }

        System.out.println("Finished loading data from files...");
        System.out.println("No of videos: "+hmVideo.size());
        System.out.println("No of channels: "+hsChannel.size());
        System.out.println("Start analysing the data...");
        //dataAnalysis(hmVideo);
    }

    static void dataAnalysis(HashMap<String, youtubeVideo> hmVideo) {

        HashMap<String, Integer> hmUser = new HashMap<String, Integer>();

        Set<String> dict = new HashSet<String>();

        long minNumberOfLike = 100000, maxNumberOfLike = 0;
        long minNumberOfDisLike = 100000, maxNumberOfDisLike = 0;
        long minNumberOfView = 100000, maxNumberOfView = 0;

        int[] bins = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        try {
            FileWriter fw;
            BufferedWriter bw;

            for (String key : hmVideo.keySet()) {
                youtubeVideo obj = hmVideo.get(key);
                //bw.write(obj.getKey()+"\t"+obj.getViewCount()+"\t"+obj.getNoOfLikes()+"\t"+obj.getNoOfDislikes()+"\n");

                Integer noOfUploadedVideo = 0;
                if (hmUser.containsKey(obj.getChannelID()))
                    noOfUploadedVideo = hmUser.get(obj.getChannelID());

                noOfUploadedVideo++;

                hmUser.put(obj.getChannelID(), noOfUploadedVideo);

                if (minNumberOfView > obj.getViewCount() && obj.getViewCount() > 0)
                    minNumberOfView = obj.getViewCount();

                if (maxNumberOfView < obj.getViewCount())
                    maxNumberOfView = obj.getViewCount();

                if (minNumberOfLike > obj.getNoOfLikes() && obj.getNoOfLikes() > 0)
                    minNumberOfLike = obj.getNoOfLikes();

                if (maxNumberOfLike < obj.getNoOfLikes() && obj.getNoOfLikes() > 0)
                    maxNumberOfLike = obj.getNoOfLikes();

                if (minNumberOfDisLike > obj.getNoOfDislikes() && obj.getNoOfDislikes() > 0)
                    minNumberOfDisLike = obj.getNoOfDislikes();

                if (maxNumberOfDisLike < obj.getNoOfDislikes() && obj.getNoOfDislikes() > 0)
                    maxNumberOfDisLike = obj.getNoOfDislikes();

                int idBin = 0;
                long threshold = 1;

                while (idBin < 13 && obj.getViewCount() > threshold) {
                    threshold *= 10;
                    idBin++;
                }

                if (idBin == 13) idBin--;
                bins[idBin]++;

                String title = obj.getTitle();
                String[] strArr = title.split(",");
                for (String str : strArr)
                    dict.add(str);

                String description = obj.getDescription();
                strArr = description.split(",");
                for (String str : strArr)
                    dict.add(str);
            }

            fw = new FileWriter("uploaders.txt");
            bw = new BufferedWriter(fw);

            for (String key : hmUser.keySet()) {
                bw.write(key+"\t"+hmUser.get(key)+"\n");
            }

            bw.close();

            // Output the dictionary
            fw = new FileWriter("dict.txt");
            bw = new BufferedWriter(fw);
            for (String word:dict)
                bw.write(word+"\n");
            bw.close();

            // Output the statistics of datasets
            fw = new FileWriter("Statistics.txt");
            bw = new BufferedWriter(fw);

            bw.write("Number of statistics:\n");
            bw.write("No of uploader: " + hmUser.size()+"\n");
            bw.write("No of videos: " + hmVideo.size()+"\n");
            bw.write("Range of views: (" + minNumberOfView +" , "+maxNumberOfView+")\n");
            bw.write("Range of likes: (" + minNumberOfLike +" , "+maxNumberOfLike+")\n");
            bw.write("Range of dislikes: (" + minNumberOfDisLike +" , "+maxNumberOfDisLike+")\n");

            long threshold = 1;
            for (int idBin = 0; idBin<13; idBin++) {
                bw.write("No of videos has view counts <=" + threshold+" is:"+ bins[idBin] + ".s\n");
                threshold*=10;
            }

            bw.write("Dictionary size: " + dict.size());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void readAndExportToCSV(String videoFolder) throws IOException{
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

                    if (processedStr.length()>0)
                        newLine += processedStr.substring(0, processedStr.length()-1)+";";
                    else
                        newLine += ";";

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

                    if (processedStr.length()>0)
                        newLine += processedStr.substring(0, processedStr.length()-1)+";";
                    else
                        newLine += ";";

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