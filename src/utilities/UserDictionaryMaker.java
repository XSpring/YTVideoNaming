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
	String videoFolder = "youtube";
	String outputFileName = "youtube/userMapSave.txt";
	try {
	    runUserDictionaryMaker(videoFolder, outputFileName);
	} catch (java.io.IOException e) {
	    System.err.println("Could not run UserDictionaryMaker: ");
	    e.printStackTrace();
	}
    }
    
    /* Saves the results of makeListOfUsersFromVideos using saveListOfUsersToFile */
    public static void runUserDictionaryMaker(String videoFolder, String outputFilename) throws java.io.IOException {
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
    
    /* Takes all videos in a list, and uses their data to construct a map of user names to youtubeUsers. */
    public static HashMap<String,youtubeUser> makeListOfUsersFromVideos(List<youtubeVideo> vidList) throws java.io.IOException {
	HashMap<String,youtubeUser> userMap = new java.util.HashMap<>();
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
