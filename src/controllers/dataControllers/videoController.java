package controllers.dataControllers;

import models.*;
import controllers.modelControllers.*;
import objects.youtubeObjects.youtubeVideo;
import utilities.Common;

import java.io.*;
import java.util.*;

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

    public static void run() throws java.io.IOException, Exception {
        //FileWriter fw = new FileWriter("videoAge1.txt");
        //BufferedWriter bw = new BufferedWriter(fw);

        //HashMap<Long, Integer> hmVideoAge = new HashMap<Long, Integer>();
        HashMap<Long, List<String>> hmVideoBins = new HashMap<Long, List<String>>();
        HashMap<String, youtubeVideo> hmVideo = dataController.getHmVideo();

        for (String vID:hmVideo.keySet()) {
            youtubeVideo video = hmVideo.get(vID);

            //bw.write(vID+"\t"+video.getHowLongAgoUploaded()+"\n");
            //bw.write(video.getHowLongAgoUploaded()+"\n");
            //Integer counter = hmVideoAge.get(video.getHowLongAgoUploaded());
            //if (counter == null)
            //    counter = 0;

            //counter ++;
            //hmVideoAge.put(video.getHowLongAgoUploaded(), counter);

            List <String> lstBin = hmVideoBins.get(video.getHowLongAgoUploaded());
            if (lstBin == null)
                lstBin = new ArrayList<String>();

            lstBin.add(vID);
            hmVideoBins.put(video.getHowLongAgoUploaded(), lstBin);
        }

        //FileWriter fw = new FileWriter("results/DirectGrad.csv");
        FileWriter fw = new FileWriter("results/LRSGA_Weighted.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        int count = 0;
        for (Long age:hmVideoBins.keySet()) {
	    if (age < 200) continue;
            List<String> lstVideos = hmVideoBins.get(age);
            //if (lstVideos.size()>=20 && lstVideos.size()<=100) {
	        if (lstVideos.size()>200) {
		        count++;
                System.out.println("Learning model with bin ("+age+").");
                modelController model = new modelController();
                model.setBw(bw);
                model.loadData(lstVideos);
                model.run("results/DirectGradDesc_"+age+"_weights");
        		if (count > 10) break;
            }
            //System.out.println(age + "\t" + hmVideoBins.get(age).size());
            //if (count==1) break;
        }

//        models.BaggingModel model = new models.BaggingModel();
//        model.setBw(bw);
//        model.run();

        bw.close();

        //readAndExportToCSV(videoFolder);
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
