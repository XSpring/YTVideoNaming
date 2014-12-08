package controllers.dataControllers;

import objects.youtubeObjects.*;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.Calendar;

/**
 * Created by larcuser on 3/10/14.
 */
public class dataController {

    static HashMap<String, youtubeVideo> hmVideo = null;
    static HashMap<String, youtubeUser> hmUser = null;

    public static void run(String videoFolder) throws IOException {
        //hmVideo  = readDataFromCSV(videoFolder);    //old version for YTVideos folder (includes comments)
        hmVideo  = utilities.DatafileGrabber.readMapOfVideos(videoFolder); //new version for data_all folder (comments excluded)
        hmUser = utilities.DatafileGrabber.readMapOfUsers(videoFolder + "/userMapSave.txt");

        /*
        long min = 10000, max = 0;
        for (String uID:hmUser.keySet()) {
            youtubeUser user = hmUser.get(uID);
            System.out.println(user.getSubscriberCount());
            if (user.getSubscriberCount()>0) {
                if (user.getSubscriberCount()>max)
                    max = user.getSubscriberCount();

                if (user.getSubscriberCount()<min)
                    min = user.getSubscriberCount();
            }
        }

        System.out.println(min+" "+max);
        */
    }

    public static HashMap<String, youtubeVideo> getHmVideo() {
        return hmVideo;
    }
    
    public static HashMap<String, youtubeUser> getHmUser() {
        return hmUser;
    }

    static HashMap<String, youtubeVideo> readDataFromCSV(String videoFolder) throws IOException {
        System.out.println("Reading in video data...");
        //List<youtubeVideo> vidList = utilities.DatafileGrabber.readListOfVideos(videoFolder);

        final File folder = new File(videoFolder);

        HashMap<String, youtubeVideo> hmVideo = new HashMap<String, youtubeVideo>();

        //HashSet<String> hsChannel = new HashSet<String>();

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
                //hsChannel.add(uploaderID);

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
        //System.out.println("No of channels: "+hsChannel.size());
        System.out.println("Start analysing the data...");
        //dataAnalysis(hmVideo);
        return hmVideo;
    }

    public static void saveToDisk(String key, String content, String fileExt) {
        try {
            String fileName = key + fileExt;

            File newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();

                FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }
        } catch (Exception e) {
            //System.out.println("Error at dataController/dataController: "+e.getMessage());
            e.printStackTrace();
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
