package objects.youtubeObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by larcuser on 3/10/14.
 */
public class youtubeUser {

    private String key;
    private String username;
    private int age;
    private String gender;
    private String aboutMe;
    private String relationship;
    private String books;
    private String company;
    private String hobbies;
    private String hometown;
    private String location;
    private String movies;
    private String music;
    private String occupation;
    private String school;
    private String channelType;
    private ArrayList<String> favorites;
    private ArrayList<String> uploads;
    private ArrayList<youtubeSubscription> subscriptions;
    private long subscriberCount;
    private long viewCount;
    private long videoWatchCount;
    private Date lastWebAccess;
    private ArrayList<String> contacts;

    public youtubeUser(String _key) {
        key = _key;
        username = "";
        age = 0;
        gender = "";
        aboutMe = "";
        relationship = "";
        books = "";
        company = "";
        hobbies = "";
        hometown = "";
        location = "";
        movies = "";
        music = "";
        occupation = "";
        school = "";
        channelType = "";
        favorites = new ArrayList<String>();
        uploads = new ArrayList<String>();
        subscriptions = new ArrayList<youtubeSubscription>();
        subscriberCount = 0;
        viewCount = 0;
        videoWatchCount = 0;
        lastWebAccess = null;
        contacts = new ArrayList<String>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getBooks() {
        return books;
    }

    public void setBooks(String books) {
        this.books = books;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMovies() {
        return movies;
    }

    public void setMovies(String movies) {
        this.movies = movies;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public ArrayList<String> getUploads() {
        return uploads;
    }

    public void setUploads(ArrayList<String> uploads) {
        this.uploads = uploads;
    }

    public ArrayList<youtubeSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<youtubeSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getVideoWatchCount() {
        return videoWatchCount;
    }

    public void setVideoWatchCount(long videoWatchCount) {
        this.videoWatchCount = videoWatchCount;
    }

    public Date getLastWebAccess() {
        return lastWebAccess;
    }

    public void setLastWebAccess(Date lastWebAccess) {
        this.lastWebAccess = lastWebAccess;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }
    
    /* Serialization methods */
    public void serializeMinimal(java.io.BufferedWriter os) throws java.io.IOException {
	    os.write(this.key);
	    os.write("   ;   ");
	    os.write("" + this.getViewCount());
	    os.newLine();
	    for (String uploadedVid : this.getUploads())
	        os.write(uploadedVid + "  ;  ");
	    os.newLine();
    }
    public static youtubeUser deserializeMinimal(java.io.BufferedReader is) throws java.io.IOException {
	    youtubeUser user = new youtubeUser("");
	    String[] pieces = is.readLine().split("   ;   ");
	    user.setKey(pieces[0]);
	    user.setViewCount(Long.parseLong(pieces[1]));
	    String[] vidNames = is.readLine().split("   ;   ");
	    java.util.ArrayList<String> uploadedVids = new java.util.ArrayList<String>();
	    uploadedVids.addAll(Arrays.asList(vidNames));
	    user.setUploads(uploadedVids);
	    return user;
    }
}
