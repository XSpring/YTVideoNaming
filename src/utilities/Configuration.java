package utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created on 2/10/14
 * @author Loc Do (larcuser)
 *
 */

/**
 * This class contains all the project settings
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private int delayTimeInSeconds;
    private GsonBuilder gsonBuilder = null;
    private Gson gson = null;
    private HashSet<String> dict = null; /* deprecated */
    private BlockingQueue<String> errVideos = null;
    private Queue<String> queuedIDs = null;
    private Set<String> crawledIDs = null;
    private int noOfConcurrentThreads;
    private final ExecutorService executor;
    private final int noOfVideosBeforeResting;
    private int maxFold;
    private int noOfIterations;
    private double eta;

    private Configuration() {
        delayTimeInSeconds = 7000;
        noOfVideosBeforeResting = 5000;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        gson = gsonBuilder.create();
        dict = new HashSet<String>();
        errVideos = new LinkedBlockingDeque<String>();
        queuedIDs = new LinkedList<String>();
        crawledIDs = Collections.synchronizedSet(new HashSet<String>());
        noOfConcurrentThreads = 7; // No of *real* cores
        executor = Executors.newFixedThreadPool(noOfConcurrentThreads);
        maxFold = 5;
        noOfIterations = 20;
        eta = 0.01;
    }

    public double getEta() {
        return eta;
    }

    public int getMaxFold() {
        return maxFold;
    }

    public int getDelayTimeInSeconds() {
        return delayTimeInSeconds;
    }

    public GsonBuilder getGsonBuilder()
    {
        return gsonBuilder;
    }

    public Gson getGson()
    {
        return gson;
    }

    public HashSet<String> getDict() { return dict; }

    public BlockingQueue<String> getErrVideos() { return errVideos; }

    public int getNoOfConcurrentThreads() {
        return noOfConcurrentThreads;
    }

    public void setDelayTimeInSeconds(int delayTimeInSeconds) {
        this.delayTimeInSeconds = delayTimeInSeconds;
    }

    public Queue<String> getQueuedIDs() {
        return queuedIDs;
    }

    public Set<String> getCrawledIDs() {
        return crawledIDs;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public int getNoOfVideosBeforeResting() { return noOfVideosBeforeResting; };

    public int getNoOfIterations() { return noOfIterations; };
}
