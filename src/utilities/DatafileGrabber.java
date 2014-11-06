package utilities;

import java.io.*;
import java.util.List;
import java.util.HashMap;
import objects.youtubeObjects.youtubeVideo;
import objects.youtubeObjects.youtubeUser;

/**
 * @author Richardson
 */
public class DatafileGrabber {
    /**
     * If folder is a folder, read all the files in that folder
     * If folder is a JSON file, create a video from that json and put to the lstVideo
     * @param folder
     */
    public static List<youtubeVideo> readListOfVideos(String folderName) throws IOException {
        final File folder = new File(folderName);
	List<youtubeVideo> list = new java.util.LinkedList<>();
        readListOfVideosRecursive(list, folder);
	return list;
    }
    
    /* A helper for createListOfVideos */
    private static void readListOfVideosRecursive(List<youtubeVideo> list, final File folder) throws IOException {
	for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                readListOfVideosRecursive(list, fileEntry);
            } else if (fileEntry.getName().indexOf(".json")>-1) {
		BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
                    String content = "";
		    String sCurrentLine;
                    while ((sCurrentLine = br.readLine()) != null) {
                        content += sCurrentLine+"\n";
                    }
                    youtubeVideo videoObj = Common.getInstance().getYTVideoObjectFromJSON(content);
                    if (videoObj!=null)
                        list.add(videoObj);
		    //System.out.println(fileEntry.getName()+" "+videoObj.getTitle()+" "+videoObj.getKey());
                    /*
                    System.out.println(videoObj.getTitle()+" "+videoObj.getNoOfLikes()
                            +" "+videoObj.getNoOfDislikes()
                            +" "+videoObj.getViewCount());
                    */
                } catch (Exception ex) {
                    ex.printStackTrace();
		    if (br != null)
			br.close();
                }
            }
        }
    }

    /* 
     */
    public static HashMap<String,youtubeVideo> readMapOfVideos(String folderName) throws IOException {
        final File folder = new File(folderName);
	List<youtubeVideo> list = new java.util.LinkedList<>();
        readListOfVideosRecursive(list, folder);
	HashMap<String,youtubeVideo> map = new java.util.HashMap<>();
	for (youtubeVideo v : list)
	    map.put(v.getKey(), v);
	return map;
    }
    
    /* Reads in user info from a single file */
    public static HashMap<String,youtubeUser> readMapOfUsers(String fileName) throws IOException {
	final File file = new File(fileName);
	HashMap<String,youtubeUser> map = new java.util.HashMap<>();
	BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
	boolean keepGoing = true;
	while (keepGoing) {
	    try {
		youtubeUser user = youtubeUser.deserializeMinimal(br);
		map.put(user.getKey(), user);
	    } catch (IOException e) {
		System.out.println("Could not read any more youtubeUsers from file "+fileName+".  Reason: ");
		System.out.println("\t"+e.getMessage());
		System.out.println("\tThis is probably normal.  Returing the list had so far.");
		keepGoing = false;
	    }
	}
	br.close();
	return map;
    }
}
