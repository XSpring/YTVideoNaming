package utilities;

import objects.youtubeObjects.youtubeUser;
import objects.youtubeObjects.youtubeVideo;
import java.util.HashMap;
import java.util.List;

/**
 * @author Richardson
 */
public class UserDictionaryMaker {
    
    public static void main(String[] args) {
	String videoFolder = "data_all";
	String outputFileName = "data_all/userMapSave.txt";
	try {
	    //old version
		//runUserDictionaryMaker(videoFolder, outputFileName);
	    //new version
		java.util.Set<String> alreadyCrawled = new java.util.HashSet<>();
		//alreadyCrawled = resumeFromEarlier();
		runGetSubscribers_oneAtATime(videoFolder, alreadyCrawled, outputFileName + "_subscribers");
		//runUserDictionaryMaker_precrawled(videoFolder, outputFileName + "_subscribers", outputFileName);
	} catch (java.io.IOException e) {
	    System.err.println("Could not run UserDictionaryMaker: ");
	    e.printStackTrace();
	}
    }
    
    /* Takes all videos in a list, and uses their data to construct a map of user names to youtubeUsers. */
    public static void runGetSubscribers_oneAtATime(String videoFolder, java.util.Set<String> alreadyCrawled, String outputFilename) throws java.io.IOException {
	final java.io.File folder = new java.io.File(videoFolder);
	java.io.File outFile = new java.io.File(outputFilename);
	int counter = 0;
	for (final java.io.File file : folder.listFiles()) {
	    if (! file.isDirectory() && file.getName().indexOf(".csv") == file.getName().length()-4) {
		java.io.File subscribersFile = new java.io.File(file.getAbsolutePath() + "_subscribers");
		if (subscribersFile.exists()) {
		    System.out.println(subscribersFile.getName() + " already exists.");
		    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(subscribersFile.getAbsolutePath()));
		    while (true) {
			String ln = br.readLine();
			if (ln == null)
			    break;
			String uName = ln.split(";")[0];
			alreadyCrawled.add(uName);
		    }
		    br.close();
		} else {
		    System.out.println("Crawling for " + file.getName());
		    java.io.BufferedReader br = null;
		    java.io.BufferedWriter bw = null;
		    try {
			br = new java.io.BufferedReader(new java.io.FileReader(file.getAbsolutePath()));
			bw = new java.io.BufferedWriter(new java.io.FileWriter(subscribersFile.getAbsolutePath()));
			while (true) {
			    youtubeVideo vid = youtubeVideo.deserializeMinimal(br);
			    if (vid == null) {
				break;
			    } else {
				String username = vid.getChannelID();
				if (! alreadyCrawled.contains(username)) {
				    long subscriptions = Crawler.getYTUserSubscriptions(username);
				    bw.write(username + ";" + subscriptions);
				    bw.newLine();
				    alreadyCrawled.add(username);
				}
				counter++;
				if (counter % 100 == 0)
				    System.out.println("makeListOfUsersFromVideos has finished " + counter + " videos so far...");
			    }
			}
		    } catch (Exception e) {
		    } finally {
			if (bw != null)
			    bw.close();
			if (br != null)
			    br.close();
		    }
		}
		System.out.println("Number of channels: "+alreadyCrawled.size());
	    }
	}
    }
    
    /* Saves the results of makeListOfUsersFromVideos using saveListOfUsersToFile */
    public static void runUserDictionaryMaker_allAtOnce(String videoFolder, String outputFilename) throws java.io.IOException {
	System.out.println("Reading in video data...");
	List<youtubeVideo> vidList = utilities.DatafileGrabber.readListOfVideos(videoFolder);
	System.out.println("" + vidList.size() + " videos found.");
	System.out.println("Creating user map data from video files...");
	HashMap<String,youtubeUser> map = makeListOfUsersFromVideos(vidList);
	System.out.println("" + map.size() + " users created from video data.");
	System.out.println("Saving user map data...");
	saveListOfUsersToFile(map, outputFilename);
	System.out.println("Done.");
    }
    
    /* Used by runUserDictionaryMaker_allAtOnce.  Takes all videos in a list, and uses their data to construct a map of user names to youtubeUsers. */
    private static HashMap<String,youtubeUser> makeListOfUsersFromVideos(List<youtubeVideo> vidList) {
	HashMap<String,youtubeUser> userMap = new java.util.HashMap<>();
	int counter = 0;
	for (youtubeVideo vid : vidList) {
	    String username = vid.getChannelID();
	    youtubeUser user = userMap.get(username);
	    if (user == null) {
		user = new youtubeUser(username);
		user.setSubscriberCount(Crawler.getYTUserSubscriptions(username));
	    }
	    user.setViewCount(user.getViewCount() + vid.getViewCount());
	    user.getUploads().add(vid.getKey());
	    userMap.put(username, user);
	    counter++;
	    if (counter % 1000 == 0)
		System.out.println("makeListOfUsersFromVideos has finished " + counter + "/" + vidList.size() + " videos.");
	}
	return userMap;
    }
    
    public static HashMap<String,youtubeUser> readListOfUsersFromFile(String filename) throws java.io.IOException {
	return utilities.DatafileGrabber.readMapOfUsers(filename);
    }
    
    public static void saveListOfUsersToFile(HashMap<String,youtubeUser> map, String filename) throws java.io.IOException {
	java.io.File outFile = new java.io.File(filename);
	java.io.BufferedWriter bw = null;
	try {
	    bw = new java.io.BufferedWriter(new java.io.FileWriter(outFile));
	    int failures = 0;
	    for (java.util.Map.Entry<String,youtubeUser> userEntry : map.entrySet()) {
		try {
		    userEntry.getValue().serializeMinimal(bw);
		} catch (java.io.IOException e) {
		    e.printStackTrace();
		    failures++;
		}
	    }
	    bw.close();
	    if (failures > 0)
		System.err.println("WARNING: " + failures + " entries of the user map failed to output correctly."
			+  " You should not trust the output file \"" + filename + "\" at all.");
	} catch (java.io.IOException e) {
	    if (bw != null)
		bw.close();
	    throw e;
	}
    }
}
