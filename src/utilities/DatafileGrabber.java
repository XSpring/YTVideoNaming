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
    public static List<youtubeVideo> readListOfVideos(String folderName) throws IOException {
	return readListOfVideos_fromDayFiles(folderName);
    }
    
    private static List<youtubeVideo> readListOfVideos_fromDayFiles(String folderName) throws IOException {
	final File folder = new File(folderName);
	List<youtubeVideo> list = new java.util.LinkedList<youtubeVideo>();
	for (final File file : folder.listFiles()) {
	    if (! file.isDirectory() && file.getName().indexOf(".csv")>-1) {
		System.out.println(file.getName());
		BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
		while (true) {
		    youtubeVideo vid = youtubeVideo.deserializeMinimal(br);
		    if (vid == null)
			break;
		    else
			list.add(vid);
		}
		br.close();
	    }
	}
	return list;
    }

    private static List<youtubeVideo> readListOfVideos_oldFolderVersion(String folderName) throws IOException {
        final File folder = new File(folderName);
	    List<youtubeVideo> list = new java.util.LinkedList<youtubeVideo>();
	    long numFailedDateReads = 0;
	    for (final File dateFolder : folder.listFiles()) {
	        if (dateFolder.isDirectory()) {
                System.out.println(dateFolder.getName());
                numFailedDateReads += readListOfVideosFromDayFolder(list, dateFolder);
            }
        }
	if (numFailedDateReads != 0)
	    System.out.println("numFailedDateReads: " + numFailedDateReads + "/" + (list.size()+numFailedDateReads));
	return list;
    }
    
    /* A helper for readListOfVideos_oldFolderVersion */
    public static long readListOfVideosFromDayFolder(List<youtubeVideo> list, final File dateFolder) throws IOException {
	String dateFolderName = dateFolder.getName();
	long numFailedDateReads = 0;
	for (final File fileEntry : dateFolder.listFiles()) {
            if (! fileEntry.isDirectory() && fileEntry.getName().indexOf(".json")>-1) {
	        	BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
                    String content = "";
		            String sCurrentLine;

                    while ((sCurrentLine = br.readLine()) != null) {
                        content += sCurrentLine+"\n";
                    }
                    youtubeVideo videoObj = Common.getInstance().getYTVideoObjectFromJSON(content);

                    if (videoObj!=null) {
			            videoObj.calculateHowLongAgoUploaded(dateFolderName);
                        videoObj.calculatedVideoLengthInSeconds();
			            if (videoObj.getHowLongAgoUploaded() == 0L ||
                            videoObj.getVideoLengthInSeconds() == 0L)
			                numFailedDateReads++;
			            else
			                list.add(videoObj);
		            }

                    br.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
		    if (br != null)
			br.close();
                }
            }
        }
	return numFailedDateReads;
    }

    /* 
     */
    public static HashMap<String,youtubeVideo> readMapOfVideos(String folderName) throws IOException {
	    List<youtubeVideo> list = readListOfVideos(folderName);
	    HashMap<String,youtubeVideo> map = new java.util.HashMap<String, youtubeVideo>();
	    for (youtubeVideo v : list)
	        map.put(v.getKey(), v);
	    return map;
    }
    
    /* Reads in user info from a single file */
    public static HashMap<String,youtubeUser> readMapOfUsers(String fileName) throws IOException {
	System.out.println("Reading in map of users...");
	final File file = new File(fileName);
	HashMap<String,youtubeUser> map = new java.util.HashMap<String, youtubeUser>();
	BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
	boolean keepGoing = true;
	while (keepGoing) {
	    try {
		    youtubeUser user = youtubeUser.deserializeMinimal(br);
		    if (user != null)
			map.put(user.getKey(), user);
		    else
			keepGoing = false;
	    } catch (IOException e) {
		    System.out.println("Could not read any more youtubeUsers from file "+fileName+".  Reason: ");
		    System.out.println("\t"+e.getMessage());
		    System.out.println("\tThis is probably normal.  Returing the list had so far.");
		    keepGoing = false;
	    }
	}
	br.close();
	System.out.println("Done reading in map of users.");
	return map;
    }
}
