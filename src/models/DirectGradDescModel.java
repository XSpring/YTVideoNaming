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

public class DirectGradDescModel extends GenericModel {
    @Override
    protected void train() {//throws Exception {
	int numIterations = 50;    //Configuration.getInstance().getNoOfIterations();
	double eta = 0.00001;	    //Configuration.getInstance().getEta();
	double lamda = 0.05;	    //Configuration.getInstance().getLambda();;
        for (int iteration = 0; iteration < numIterations; iteration++) {
            FeatureController gradient = new FeatureController();
            for (int idI1=0; idI1 < trainData.size(); idI1++) {
		objects.youtubeObjects.youtubeVideo ytVid = dataController.getHmVideo().get(trainData.get(idI1));
		FeatureController datapoint = FeatureController.getFeatureControllerFromVid_1(ytVid);
		double prediction = FeatureController.getInnerProduct(datapoint, modelParams);
		double diffrence = prediction - ytVid.getViewCount();
		gradient.addWithScaling(datapoint, diffrence);
	    }
	    modelParams.addWithScaling(modelParams, -1.0 * eta);
	    modelParams.addWithScaling(gradient, -1.0 * eta);
	    //output for debug
	    double gradientMag = Math.sqrt(FeatureController.getInnerProduct(gradient, gradient));
	    double errSq = this.getErrSq(getPredictions(trainData), trainData);
	    System.out.println("Iter: " + iteration + "; ErrSq: " + errSq + "; Gradient mag: " + gradientMag);
        }
    }

    @Override
    protected void test(boolean onTestData) {//throws Exception {
	List<Object> data = onTestData ? testData : trainData;
	java.util.List<Double> predictions = getPredictions(data);
	double sqrtErrSq = getErrSq(predictions, data);
	long correct = getNumCorrect(predictions, data);
	long count = predictions.size() * predictions.size() / 2;
	long bin = dataController.getHmVideo().get(data.get(0)).getHowLongAgoUploaded();
	outputResultsLine(onTestData, bin, sqrtErrSq, correct, count);
    }
    
    public java.util.List<Double> getPredictions(java.util.List<Object> vids) {
	java.util.List<Double> list = new java.util.ArrayList<Double>();
	for (int i=0; i < vids.size(); i++) {
	    objects.youtubeObjects.youtubeVideo ytVid = dataController.getHmVideo().get(vids.get(i));
	    FeatureController datapoint = FeatureController.getFeatureControllerFromVid_1(ytVid);
	    list.add(FeatureController.getInnerProduct(datapoint, modelParams));
	}
	return list;
    }
    
    private double getErrSq(java.util.List<Double> predictions, List<Object> data) {
	double errSq = 0;
        int count = 0;
        for (int i=0; i < data.size(); i++) {
	    double prediction = predictions.get(i);
	    double truth = dataController.getHmVideo().get(data.get(i)).getNoOfLikes();
	    double diffrence = prediction - truth;
	    errSq += diffrence*diffrence;
	    count++;
	}
        return Math.sqrt(errSq/count);
    }
    
    private long getNumCorrect(java.util.List<Double> predictions, List<Object> data) {
	int correct = 0;
	for (int i=0; i < predictions.size()-1; i++) {
            for (int j=i+1; j < predictions.size(); j++) {
		boolean iBiggerPredicted = predictions.get(i) > predictions.get(j);
		long c_i = dataController.getHmVideo().get(data.get(i)).getViewCount();
		long c_j = dataController.getHmVideo().get(data.get(j)).getViewCount();
		boolean iBiggerTruth = c_i > c_j;
		if (iBiggerPredicted == iBiggerTruth)
		    correct++;
	    }
	}
	return correct;
    }
    
    private long avgDataSize(List<Object> data) {
	long total = 0;
	for (int i=0; i < data.size(); i++)
	    total += dataController.getHmVideo().get(data.get(i)).getNoOfLikes();
	return total / data.size();
    }
}
