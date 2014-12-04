package controllers.dataControllers;

import java.util.HashMap;
import java.util.HashSet;

import objects.youtubeObjects.*;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 */
public class FeatureController {
    public static final int NUMERICFEATURES = 0;
    public static final int BAGOFWORDSFEATURES = 1;
    public static final int CHANNELIDFEATURES = 2;
    public static final int CATEGORYFEATURES = 3;
    
    HashMap<Integer, Double> hmNumericFeatures = null; // type = 0
    HashMap<String, Double> hmBoWFeatures = null; // type = 1
    HashMap<String, Double> hmChannelIDFeatures = null; // type = 2
    HashMap<String, Double> hmCategoryFeatures = null; // type = 3

    public FeatureController() {
        hmNumericFeatures = new HashMap<Integer, Double>();
        hmBoWFeatures = new HashMap<String, Double>();
        hmChannelIDFeatures = new HashMap<String, Double>();
        hmCategoryFeatures = new HashMap<String, Double>();
    }

    public Double getOrInitFeature(int featureType, Object key) {
        Double value = 0.0;
        switch (featureType) {
            case 0: value = hmNumericFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmNumericFeatures.put((Integer)key, value);
                    }
                    break;
            case 1: value = hmBoWFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmBoWFeatures.put((String)key, value);
                    }
                    break;
            case 2: value = hmChannelIDFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmChannelIDFeatures.put((String)key, value);
                    }
                    break;
            case 3: value = hmCategoryFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmCategoryFeatures.put((String)key, value);
                    }
                    break;
        }
        return value;
    }

    public boolean setFeature(int featureType, Object key, Object value) {
        try {
            switch (featureType) {
                case 0: hmNumericFeatures.put((Integer) key, (Double) value);
                        break;
                case 1: hmBoWFeatures.put((String) key, (Double) value);
                        break;
                case 2: hmChannelIDFeatures.put((String) key, (Double) value);
                        break;
                case 3: hmCategoryFeatures.put((String) key, (Double) value);
                        break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public HashMap<Integer, Double> getNumericFeatures() {
        return hmNumericFeatures;
    }
    
    public HashMap<String, Double> getStringFeatures(int featureType) {
        switch (featureType) {
            case 1: return hmBoWFeatures;
            case 2: return hmChannelIDFeatures;
            case 3: return hmCategoryFeatures;
        }
        return null;
    }

    public HashMap<Integer, Double> getHmNumericFeatures() {
        return hmNumericFeatures;
    }

    public HashMap<String, Double> getHmBoWFeatures() {
        return hmBoWFeatures;
    }

    public HashMap<String, Double> getHmChannelIDFeatures() {
        return hmChannelIDFeatures;
    }

    public HashMap<String, Double> getHmCategoryFeatures() {
        return hmCategoryFeatures;
    }
    
    public static double getInnerProduct(FeatureController f1, FeatureController f2) {
	double innerProd = 0.0;
	for (Integer idF:f1.getHmNumericFeatures().keySet())
	    innerProd += f2.getOrInitFeature(0, idF) * f1.getOrInitFeature(0, idF);
	for (int featureType=1; featureType<4; featureType++) {
	    for (String key:f1.getStringFeatures(featureType).keySet())
		innerProd += f2.getOrInitFeature(featureType, key) * f1.getOrInitFeature(featureType, key);
	}
	return innerProd;
    }
    
    public void addWithScaling(FeatureController f2, double scale) {
	for (int featureType=0; featureType<4; featureType++) {
	    for (Object k: (featureType==0 ? f2.getNumericFeatures() : f2.getStringFeatures(featureType)).keySet()) {
		double feat = this.getOrInitFeature(featureType, k);
		double toAdd = f2.getOrInitFeature(featureType, k) * scale;
		this.setFeature(featureType, k, feat + toAdd);
	    }
	}
    }
    
    public void output(String filename) {
	java.io.FileWriter fw = null;
	java.io.BufferedWriter bw = null;
	try {
	    fw = new java.io.FileWriter(filename);
	    bw = new java.io.BufferedWriter(fw);
	    for (Integer j : hmNumericFeatures.keySet()) {
		bw.write("0." + j + ";" + hmNumericFeatures.get(j));
		bw.newLine();
	    }
	    for (int i=1; i<4; i++) {
		for (String j : getStringFeatures(i).keySet()) {
		    bw.write(i + "." + j + ";" + getOrInitFeature(i, j));
		    bw.newLine();
		}
	    }
	} catch (java.io.IOException e) {
	    System.err.println("Failed to finish output file " + filename);
	} finally {
	    try {
		bw.close();
		fw.close();
	    } catch (java.io.IOException e) {};
	}
    }
    
    //made by Joseph
    public static FeatureController getFeatureControllerFromVid_1(youtubeVideo ytVid) {
	// Create representative feature vector
	FeatureController X_i = new FeatureController();

	// 0. Numeric features
	java.util.Map<String, youtubeUser> userList = dataController.getHmUser();
	youtubeUser uploader = userList.get(ytVid.getChannelID());
	long totalUploaderNumVidViews = uploader.getVideoWatchCount();
	long totalUploaderNumVids = 0;
	for (String vName : uploader.getUploads()) {
	    youtubeVideo v = dataController.getHmVideo().get(vName);
	    if (v != null) {
		long v_daysAgoPublished = v.getHowLongAgoUploaded();
		if (v_daysAgoPublished < ytVid.getHowLongAgoUploaded())
		    totalUploaderNumVidViews -= v.getViewCount();
		else
		    totalUploaderNumVids++;
	    }
	}
	double viewsPerVid = (totalUploaderNumVids!=0) ? ((double)totalUploaderNumVidViews/totalUploaderNumVids) : 0;
	double likeDislikeRatio = ytVid.getNoOfDislikes()!=0 ? (1.0*ytVid.getNoOfLikes()/(ytVid.getNoOfLikes() +
                                                                ytVid.getNoOfDislikes())) : 0.0;
	X_i.getHmNumericFeatures().put(0, 1.0);
	X_i.getHmNumericFeatures().put(1, Math.log(likeDislikeRatio));
	X_i.getHmNumericFeatures().put(2,(double)ytVid.getVideoLengthInSeconds());
	X_i.getHmNumericFeatures().put(3,(double)ytVid.getHowLongAgoUploaded());
	X_i.getHmNumericFeatures().put(4,(double)uploader.getSubscriberCount());
	X_i.getHmNumericFeatures().put(5,(double)totalUploaderNumVids);
	X_i.getHmNumericFeatures().put(6,(double)totalUploaderNumVidViews);
	X_i.getHmNumericFeatures().put(7,viewsPerVid);
	

	// 1. Bag of Words (from Title only)
	String[] titleArr = ytVid.getTitle().split(",");
	for (String str:titleArr) {
	    Double tf = X_i.getHmBoWFeatures().get(str);
	    if (tf == null)
		tf = 0.0;
	    tf++;
	    X_i.getHmBoWFeatures().put(str, tf);
	}

	// 2. Category
	Double tf = X_i.getHmCategoryFeatures().get(ytVid.getCategory());
	if (tf == null)
	    tf = 0.0;
	tf++;
	X_i.getHmCategoryFeatures().put(ytVid.getCategory(), tf);

	// 3. Uploader ID
	//I skip this one, since we instead extracted numeric features

	return X_i;
    }
    
    //made by Loc
    public static FeatureController getFeatureControllerFromVids_0(youtubeVideo ytVid1, youtubeVideo ytVid2) {
	// Create representative feature vector
	FeatureController X_ij = new FeatureController();

	// 1. Numeric features
	// 1.0 Intercept weight w_0
	X_ij.getHmNumericFeatures().put(0, 1.0);
	/*
	// 1.1 No of likes
	X_ij.getHmNumericFeatures().put(1, 1.0*(ytVid1.getNoOfLikes() / ytVid2.getNoOfLikes()));
	// 1.2 No of dislikes
	X_ij.getHmNumericFeatures().put(2, 1.0*(ytVid1.getNoOfDislikes() / ytVid2.getNoOfDislikes()));
	*/

	// 1.3 Video Length In Seconds
	//X_ij.getHmNumericFeatures().put(3, 1.0 * ytVid1.getVideoLengthInSeconds() / ytVid2.getVideoLengthInSeconds());

	// 2. Bag of Words (from Title only)
	String[] titleArr = ytVid1.getTitle().split(",");
	for (String str:titleArr) {
	    Double tf = X_ij.getHmBoWFeatures().get(str);
	    if (tf == null)
		tf = 0.0;
	    tf++;
	    X_ij.getHmBoWFeatures().put(str, tf);
	}
	int titleLength1 = titleArr.length;
	titleArr = ytVid2.getTitle().split(",");
	for (String str:titleArr)
	{
	    Double tf = X_ij.getHmBoWFeatures().get(str);
	    if (tf == null)
		tf = 0.0;
	    tf--;
	    X_ij.getHmBoWFeatures().put(str, tf);
	}
	int titleLength2 = titleArr.length;
	//X_ij.getHmNumericFeatures().put(4, 1.0 * titleLength1 / titleLength2);

	// 3. Category
	/*
	Double tf = X_ij.getHmCategoryFeatures().get(ytVid1.getCategory());
	if (tf == null)
	    tf = 0.0;
	tf++;
	X_ij.getHmCategoryFeatures().put(ytVid1.getCategory(), tf);

	tf = X_ij.getHmCategoryFeatures().get(ytVid2.getCategory());
	if (tf == null)
	    tf = 0.0;
	tf--;
	X_ij.getHmCategoryFeatures().put(ytVid2.getCategory(), tf);
	*/

	// 4. Uploader ID
    java.util.Map<String, youtubeUser> userList = dataController.getHmUser();

    youtubeUser uploader1 = userList.get(ytVid1.getChannelID());
    youtubeUser uploader2 = userList.get(ytVid1.getChannelID());

	Double tf = X_ij.getHmChannelIDFeatures().get(ytVid1.getChannelID());
	if (tf == null)
	    tf = 0.0;
	tf++;
	//X_ij.getHmChannelIDFeatures().put(ytVid1.getChannelID(), tf);
	tf = X_ij.getHmChannelIDFeatures().get(ytVid2.getChannelID());
	if (tf == null)
	    tf = 0.0;
	tf--;
	//X_ij.getHmChannelIDFeatures().put(ytVid2.getChannelID(), tf);

    //X_ij.getHmNumericFeatures().put(5, 1.0*uploader1.getSubscriberCount() - uploader2.getSubscriberCount());
	return X_ij;
    }
    
    //made by Joseph, to match getFeatureControllerFromVid_1
    public static FeatureController getFeatureControllerFromVids_1(youtubeVideo ytVid1, youtubeVideo ytVid2) {
	// Create representative feature vector
	FeatureController X_ij = new FeatureController();
	
	FeatureController X_i = getFeatureControllerFromVid_1(ytVid1);
	FeatureController X_j = getFeatureControllerFromVid_1(ytVid2);
	
	// 0. Numeric features
	java.util.Set<Integer> numFeatureKeys = X_i.getNumericFeatures().keySet();
	for (Integer k : numFeatureKeys) {
	    double f_i = X_i.getHmNumericFeatures().get(k);
	    double f_j = X_j.getHmNumericFeatures().get(k);
	    X_ij.getNumericFeatures().put(k, f_i - f_j);
	    X_ij.getNumericFeatures().put(numFeatureKeys.size() + k, f_i / f_j);
	}
	
	// Bag of words features
	for (int featType=1; featType<4; featType++) {
        // Need to fix this since my JRE cannot interpret the addAll

	    java.util.Set<String> strFeatureKeys = new HashSet<String>();

        for (String str:X_i.getStringFeatures(featType).keySet())
            strFeatureKeys.add(str);


        for (String str:X_j.getStringFeatures(featType).keySet())
	        strFeatureKeys.add(str);

	    for (String k : strFeatureKeys) {
		double f_i = X_i.getOrInitFeature(featType, k);
		double f_j = X_j.getOrInitFeature(featType, k);
		X_ij.setFeature(featType, k, f_i - f_j);
	    }
	}

	return X_i;
    }
}
