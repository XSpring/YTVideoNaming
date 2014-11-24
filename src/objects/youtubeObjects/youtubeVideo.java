package objects.youtubeObjects;

import java.text.ParseException;
import java.util.ArrayList;

import utilities.Common;
import utilities.Configuration;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by larcuser on 2/10/14.
 */
public class youtubeVideo {

    private String key;

    private String title;
    private String description;
    private String publishedDate;

    private long viewCount;
    protected int noOfLikes;
    protected int noOfDislikes;

    protected ArrayList<String> relatedURL;

    private String category;
    private String licence;

    private ArrayList<youtubeVideoComment> comments;
    private double latitude;
    private double longitude;
    private String duration;

    private String channelID;
    private String authorYTURL;
    private String authorGPlusURL;
    private boolean isFamilyFriendly;
    private String regionAllowed;

    private boolean isPaid;
    private boolean unlisted;
    
    private long howLongAgoUploaded;
    private long lengthInSeconds;

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isUnlisted() {
        return unlisted;
    }

    public void setUnlisted(boolean unlisted) {
        this.unlisted = unlisted;
    }

    public youtubeVideo(String _key) {
        key = _key;

        title = "";
        description = "";
        publishedDate = "";

        viewCount = 0;
        noOfDislikes = 0;
        noOfLikes = 0;

        relatedURL = new ArrayList<String>();

        category = "";
        licence = "";

        longitude = 0.0;
        latitude = 0.0;
        duration = "";

        comments = new ArrayList<youtubeVideoComment>();

        channelID = "";

        authorYTURL = "";
        authorGPlusURL = "";

        isFamilyFriendly = true;
        regionAllowed = "";

        isPaid = false;
        unlisted = false;

        lengthInSeconds = 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public int getNoOfDislikes() {
        return noOfDislikes;
    }

    public void setNoOfDislikes(int noOfDislikes) {
        this.noOfDislikes = noOfDislikes;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public ArrayList<String> getRelatedURL() {
        return relatedURL;
    }

    public void setRelatedURL(ArrayList<String> relatedURL) {
        this.relatedURL = relatedURL;
    }

    public String getCategory() {
        return category;
    }

    public void set
            (String category) {
        this.category = category;
    }

    public ArrayList<youtubeVideoComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<youtubeVideoComment> comments) {
        this.comments = comments;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void retrieveDataFromJSON() {

    }

    public String exportDataToJSON() {
        return Configuration.getInstance().getGson().toJson(this);
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getAuthorYTURL() {
        return authorYTURL;
    }

    public void setAuthorYTURL(String authorYTURL) {
        this.authorYTURL = authorYTURL;
    }

    public String getAuthorGPlusURL() {
        return authorGPlusURL;
    }

    public void setAuthorGPlusURL(String authorGPlusURL) {
        this.authorGPlusURL = authorGPlusURL;
    }

    public boolean isFamilyFriendly() {
        return isFamilyFriendly;
    }

    public void setFamilyFriendly(boolean isFamilyFriendly) {
        this.isFamilyFriendly = isFamilyFriendly;
    }

    public String getRegionAllowed() {
        return regionAllowed;
    }

    public void setRegionAllowed(String regionAllowed) {
        this.regionAllowed = regionAllowed;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }
    
    public long getHowLongAgoUploaded() {
	return howLongAgoUploaded;
    }
    
    public void setHowLongAgoUploaded(long l) {
	howLongAgoUploaded = l;
    }
    
    public void calculateHowLongAgoUploaded(String crawlFolderName) throws ParseException {
	    Calendar cCal = new java.util.GregorianCalendar();
	    Date cDate = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.ENGLISH).parse(crawlFolderName);
	    cCal.setTime(cDate);

	    //parse upload date.  Things get ugly...
	    String uDateAsString = this.getPublishedDate();
	    Calendar uCal = new java.util.GregorianCalendar();
	    java.util.Locale locale = java.util.Locale.ENGLISH;
	    String formatExpected = "MMMM d, yyyy";
	    if (uDateAsString.startsWith("Published on")) {
    	    uDateAsString = uDateAsString.replaceAll("Published on ", "");
	    } else if (uDateAsString.startsWith("Uploaded on")) {
	        uDateAsString = uDateAsString.replaceAll("Uploaded on ", "");
	    } else if (uDateAsString.startsWith("Diterbitkan tanggal")) {
            uDateAsString = uDateAsString.replaceAll("Diterbitkan tanggal ", "");
            uDateAsString = Common.convertDateTimeIndoToEn(uDateAsString);
            formatExpected = "dd MMM yyyy";
        } else if (uDateAsString.startsWith("Diupload tanggal")) {
            uDateAsString = uDateAsString.replaceAll("Diupload tanggal ", "");
            uDateAsString = Common.convertDateTimeIndoToEn(uDateAsString);
            formatExpected = "dd MMM yyyy";
        } else if (uDateAsString.startsWith("Streaming langsung tanggal")) {
            uDateAsString = uDateAsString.replaceAll("Streaming langsung tanggal ", "");
            uDateAsString = Common.convertDateTimeIndoToEn(uDateAsString);
            formatExpected = "dd MMM yyyy";
        } else if (uDateAsString.startsWith("Dijadwalkan untuk tanggal")) {
            uDateAsString = uDateAsString.replaceAll("Dijadwalkan untuk tanggal ", "");
            uDateAsString = Common.convertDateTimeIndoToEn(uDateAsString);
            formatExpected = "dd MMM yyyy";
        } else if (uDateAsString.startsWith("Streamed live on")) {
            uDateAsString = uDateAsString.replaceAll("Streamed live on ", "");
        }
        else {
	        System.out.println("Unparsable format found: " + uDateAsString);
	        formatExpected = "";
	    }

	    if (! formatExpected.isEmpty()) {
	        Date uDate = new java.text.SimpleDateFormat(formatExpected, locale).parse(uDateAsString);
	        uCal.setTime(uDate);
	        long diffInMs = cCal.getTimeInMillis() - uCal.getTimeInMillis();
	        long diffInDays = diffInMs/(1000*60*60*24);
	        howLongAgoUploaded = diffInDays;
	    } else {
	        howLongAgoUploaded = 0;
	    }
    }

    public long getVideoLengthInSeconds() {
        return lengthInSeconds;
    }

    public void calculatedVideoLengthInSeconds() {
        int hour = 0;
        int minute = 0;
        int second = 0;
        String length = duration;
        //String mydata = "some string with 'the data i want' inside";
        Pattern pattern = Pattern.compile("PT(.*?)M");
        Matcher matcher = pattern.matcher(duration);
        if (matcher.find())
        {
            //System.out.println(matcher.group(1));
            minute = Integer.parseInt(matcher.group(1));
        }

        pattern = Pattern.compile("M(.*?)S");
        matcher = pattern.matcher(duration);
        if (matcher.find())
        {
            //System.out.println(matcher.group(1));
            second = Integer.parseInt(matcher.group(1));
        }

        lengthInSeconds = hour * 3600 + minute * 60 + second;
    }
}
