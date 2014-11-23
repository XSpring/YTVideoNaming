package Predictors;
import java.util.HashMap;
import java.util.List;
import objects.youtubeObjects.youtubeVideo;
import objects.youtubeObjects.youtubeUser;
import utilities.DatafileGrabber;
import utilities.Common;
import java.util.ArrayList;

/**
 * @author Richardson
 */
public class MidwayReportSimpleRegression extends Predictor {
    //the number of features and the number of weights should be the same.  There is no check for this, however...
    ArrayList<Double> weights_LogNumViews;
    ArrayList<Double> weights_PercentLikes;
    ArrayList<Double> weights_PercentDislikes;
    HashMap<String,youtubeVideo> allVidMap;
    HashMap<String,youtubeUser> uploaderMap;
    
    public static void main(String[] args) throws java.io.IOException {
	String videoFolderName = "youtube";
	String uploaderFileName = "youtube/userMapSave.txt";
	int numRuns = 1;
	int splitPercent = 80;
	
	List<youtubeVideo> vidList = DatafileGrabber.readListOfVideos(videoFolderName);
	List<Common.Pair<Predictor,Double>> predictors = new ArrayList<Common.Pair<Predictor,Double>>(numRuns);  //precitors and their losses
	java.util.Random rand = new java.util.Random();	
	for (int i=0; i<numRuns; i++) {
	    Common.Pair<List<youtubeVideo>,List<youtubeVideo>> trainingAndTesting = Common.splitToTestAndTraining(vidList, vidList.size()*splitPercent/100, rand);
	    List<youtubeVideo> trainingVids = trainingAndTesting.first;
	    List<youtubeVideo> testingVids = trainingAndTesting.second;
	    Predictor pred = new MidwayReportSimpleRegression(videoFolderName, uploaderFileName);
	    pred.train(trainingVids);
	    double loss = pred.getLossLogNumViews(testingVids);
	    predictors.add(new Common.Pair<Predictor,Double>(pred, loss));
	}
	//do what we want with predictors, now.
    }
    
    public MidwayReportSimpleRegression(String videoFolderName, String uploaderFileName) throws java.io.IOException {
	allVidMap = DatafileGrabber.readMapOfVideos(videoFolderName);
	uploaderMap = DatafileGrabber.readMapOfUsers(uploaderFileName);
    }

    @Override
    public double predictNumViews(youtubeVideo vid) {
	return Math.exp(predictLogNumViews(vid));
    }

    @Override
    public double predictPercentLikes(youtubeVideo vid) {
	ArrayList<Double> features = getFeatures(vid);
	double sum = 0;
	for (int i=0; i<features.size(); i++)
	    sum += features.get(i) * weights_PercentLikes.get(i);
	return sum;
    }

    @Override
    public double predictPercentDislikes(youtubeVideo vid) {
	ArrayList<Double> features = getFeatures(vid);
	double sum = 0;
	for (int i=0; i<features.size(); i++)
	    sum += features.get(i) * weights_PercentDislikes.get(i);
	return sum;
    }

    @Override
    public double predictLogNumViews(youtubeVideo vid) {
	ArrayList<Double> features = getFeatures(vid);
	double sum = 0;
	for (int i=0; i<features.size(); i++)
	    sum += features.get(i) * weights_LogNumViews.get(i);
	return sum;
    }
    
    @Override
    public void train(List<youtubeVideo> trainVids) {
	System.err.println("MidwayReportSimpleRegression.train() not yet finished.");
    }

    private ArrayList<Double> getFeatures(youtubeVideo vid) {
	ArrayList<Double> nonBagOfWordFeaters = getNonBagOfWordsFeatures(vid);
	ArrayList<Double> bagOfWordFeaters = getBagOfWordsFeatures(vid);
	nonBagOfWordFeaters.addAll(bagOfWordFeaters);
	return nonBagOfWordFeaters;
    }
    
    private ArrayList<Double> getNonBagOfWordsFeatures(youtubeVideo vid) {
	ArrayList<Double> features = new ArrayList<Double>();
	int duration = Common.getSecondsLong(vid.getDuration());
	long daysAgoPublished = vid.getHowLongAgoUploaded();
	String uploaderStr = vid.getChannelID();
	youtubeUser uploader = uploaderMap.get(uploaderStr);
	long totalUploaderNumVidViews = uploader.getVideoWatchCount();
	long totalUploaderNumVids = 0;
	for (String vName : uploader.getUploads()) {
	    youtubeVideo v = allVidMap.get(vName);
	    long v_daysAgoPublished = v.getHowLongAgoUploaded();
	    if (v_daysAgoPublished < daysAgoPublished)
		totalUploaderNumVidViews -= v.getViewCount();
	    else
		totalUploaderNumVids++;
	}
	double ratio = (totalUploaderNumVids!=0) ? ((double)totalUploaderNumVidViews/totalUploaderNumVids) : 0;
	features.add((double)duration);
	features.add((double)daysAgoPublished);
	features.add((double)totalUploaderNumVids);
	features.add((double)totalUploaderNumVidViews);
	features.add(ratio);
	return features;
    }
    
    private ArrayList<Double> getBagOfWordsFeatures(youtubeVideo vid) {
	ArrayList<Double> features = new ArrayList<Double>();
	String title = vid.getTitle();
	System.err.println("MidwayReportSimpleRegression.getBagOfWordsFeatures() not done.");
	return features;
    }
}
