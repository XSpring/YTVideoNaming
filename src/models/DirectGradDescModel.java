package models;

import controllers.dataControllers.dataController;
import controllers.dataControllers.FeatureController;
import utilities.Configuration;
import objects.youtubeObjects.*;

import java.util.List;

/**
 * Created on 1/12/14
 * Linear Regression using Gradient Descent
 */

public class DirectGradDescModel extends genericModel {
    FeatureController modelParams = null;

    @Override
    public void run(List<Object> trainData, List<Object> testData) {
        this.trainData = trainData;
        this.testData = testData;
        modelParams = new FeatureController();
	System.out.println("Training...");
	train();
	System.out.println("Testing...");
	test();
    }

    @Override
    void train() {//throws Exception {
        for (int iteration = 0; iteration < Configuration.getInstance().getNoOfIterations(); iteration++) {
            FeatureController gradient = new FeatureController();
	    //update the gradient, one data point at a time
            for (int idI1=0; idI1 < trainData.size(); idI1++) {
		objects.youtubeObjects.youtubeVideo ytVid = dataController.getHmVideo().get(trainData.get(idI1));
		FeatureController datapoint = getFeatureController(ytVid);

		// Compute the inner product of modelParams and datapoint
		double innerProd = 0.0;
		for (Integer idF:datapoint.getHmNumericFeatures().keySet())
		    innerProd += modelParams.getOrInitFeature(0, idF) * datapoint.getOrInitFeature(0, idF);
		for (int featureType=1; featureType<4; featureType++) {
		    for (String key:datapoint.getStringFeatures(featureType).keySet())
			innerProd += modelParams.getOrInitFeature(featureType, key) * datapoint.getOrInitFeature(featureType, key);
		}
		double diffrence = innerProd - ytVid.getViewCount();

		// Update the gradient
		for (Integer idF:datapoint.getHmNumericFeatures().keySet()) {
		    double g_feat = gradient.getOrInitFeature(0, idF);
		    g_feat += datapoint.getOrInitFeature(0, idF) * diffrence;
		    gradient.setFeature(0, idF, g_feat);
		}
		for (int featureType=1; featureType<4; featureType++) {
		    for (String key:datapoint.getStringFeatures(featureType).keySet()) {
			double g_feat = gradient.getOrInitFeature(featureType, key);
			g_feat += datapoint.getOrInitFeature(featureType, key) * diffrence;
			gradient.setFeature(featureType, key, g_feat);
		    }
		}
	    }

            // Update the parameters
            double w_feat = 0.0;
            for (Integer idF:modelParams.getHmNumericFeatures().keySet()) {
                w_feat = modelParams.getOrInitFeature(0, idF);
                w_feat += Configuration.getInstance().getEta() * gradient.getOrInitFeature(0, idF);
                modelParams.setFeature(0, idF, w_feat);
            }
            for (int featureType=1; featureType<4; featureType++) {
                for (String key:modelParams.getStringFeatures(featureType).keySet()) {
                    w_feat = modelParams.getOrInitFeature(featureType, key);
                    w_feat += Configuration.getInstance().getEta() * gradient.getOrInitFeature(featureType, key);
                    modelParams.setFeature(featureType, key, w_feat);
                }
            }
        }
    }

    @Override
    void test() {//throws Exception {
	java.util.List<Double> predictions = getPredictions(testData);
	//get direct sqrtErrSq results
	double errSq = 0;
        int count = 0;
        for (int i=0; i < testData.size(); i++) {
	    double prediction = predictions.get(i);
	    double truth = dataController.getHmVideo().get(testData.get(i)).getNoOfLikes();
	    double diffrence = prediction - truth;
	    errSq += diffrence*diffrence;
	    count++;
	}
        double sqrtErrSq = Math.sqrt(errSq/count);
	//count comparison results
	int correct = 0;
	count = 0;
	for (int i=0; i < predictions.size()-1; i++) {
            for (int j=i+1; j < predictions.size(); j++) {
		count++;
		boolean iBiggerPredicted = predictions.get(i) > predictions.get(j);
		long c_i = dataController.getHmVideo().get(testData.get(i)).getViewCount();
		long c_j = dataController.getHmVideo().get(testData.get(j)).getViewCount();
		boolean iBiggerTruth = c_i > c_j;
		if (iBiggerPredicted == iBiggerTruth)
		    correct++;
	    }
	}
	//output the results
	try {
	    bw.write("Results:");
	    bw.write("  sqrtErrSq = " + sqrtErrSq);
	    bw.write("  # correct = " + correct + "/" + count);
	} catch (Exception e) { }
    }
    
    @Override
    public void output(String filename) {
	modelParams.output(filename);
    }
    
    private java.util.List<Double> getPredictions(java.util.List<Object> vids) {
	java.util.List<Double> list = new java.util.ArrayList<Double>(vids.size());
	for (int i=0; i < vids.size(); i++) {
	    objects.youtubeObjects.youtubeVideo ytVid = dataController.getHmVideo().get(vids.get(i));
	    FeatureController datapoint = getFeatureController(ytVid);
	    double innerProd = 0.0;
	    for (Integer idF:datapoint.getHmNumericFeatures().keySet())
		innerProd += modelParams.getOrInitFeature(0, idF) * datapoint.getOrInitFeature(0, idF);
	    for (int featureType=1; featureType<4; featureType++) {
		for (String key:datapoint.getStringFeatures(featureType).keySet())
		    innerProd += modelParams.getOrInitFeature(featureType, key) * datapoint.getOrInitFeature(featureType, key);
	    }
	    list.set(i, innerProd);
	}
	return list;
    }
    
    private FeatureController getFeatureController(youtubeVideo ytVid) {
	// Create representative feature vector
	FeatureController X_i = new FeatureController();

	// 0. Numeric features
	youtubeUser uploader = dataController.getHmUser().get(ytVid.getChannelID());
	long totalUploaderNumVidViews = uploader.getVideoWatchCount();
	long totalUploaderNumVids = 0;
	for (String vName : uploader.getUploads()) {
	    youtubeVideo v = dataController.getHmVideo().get(vName);
	    long v_daysAgoPublished = v.getHowLongAgoUploaded();
	    if (v_daysAgoPublished < ytVid.getHowLongAgoUploaded())
		totalUploaderNumVidViews -= v.getViewCount();
	    else
		totalUploaderNumVids++;
	}
	double ratio = (totalUploaderNumVids!=0) ? ((double)totalUploaderNumVidViews/totalUploaderNumVids) : 0;
	X_i.getHmNumericFeatures().put(0, 1.0);
	X_i.getHmNumericFeatures().put(1, 1.0*ytVid.getNoOfLikes());
	X_i.getHmNumericFeatures().put(2, 1.0*ytVid.getNoOfDislikes());
	X_i.getHmNumericFeatures().put(3,(double)ytVid.getVideoLengthInSeconds());
	X_i.getHmNumericFeatures().put(4,(double)ytVid.getHowLongAgoUploaded());
	X_i.getHmNumericFeatures().put(5,(double)totalUploaderNumVids);
	X_i.getHmNumericFeatures().put(6,(double)totalUploaderNumVidViews);
	X_i.getHmNumericFeatures().put(7,ratio);

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
}
