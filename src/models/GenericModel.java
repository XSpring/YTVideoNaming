package models;

import controllers.dataControllers.FeatureController;
import controllers.dataControllers.dataController;
import objects.youtubeObjects.youtubeVideo;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 1/11/14.
 * @author Loc Do
 */

public abstract class GenericModel {
    protected List<Object> trainData = null;
    protected List<Object> testData = null;
    protected BufferedWriter bw = null;
    protected FeatureController modelParams = null;
    
    protected abstract void train();
    protected abstract void test(boolean onTestData);

    public GenericModel() {modelParams = new FeatureController();}

    public GenericModel(List<Object> trainData, List<Object> testData) {
        this.trainData = trainData;
        this.testData = testData;
        modelParams = new FeatureController();
    }

    public void run(List<Object> trainData, List<Object> testData, String whereSaveModel) {
        this.trainData = trainData;
        this.testData = testData;
        modelParams = new FeatureController();
	System.out.println("Training...");
	train();
	System.out.println("Testing and saving params...");
	test(false);
	test(true);
	if (!whereSaveModel.isEmpty())
	    outputModelParams(whereSaveModel);
    }

    public FeatureController getModelParams() { return modelParams;}

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }
    
    protected void outputModelParams(String filename) {
	modelParams.output(filename);
    }
    
    protected void outputResultsLine(boolean onTestData, long bin, double sqrtErrSq, long correct, long count) {
	try {
	    bw.newLine();
	    bw.write((onTestData ? "testing " : "training") + ",");
	    bw.write("" + bin + ",");
	    bw.write("" + sqrtErrSq + ",");
	    bw.write("" + 1.0*correct/count + ",");
	    bw.write("" + correct + ",");
	    bw.write("" + count);
	} catch (java.io.IOException e) { }
    }
    
    private long avgDataSize(List<Object> data) {
	long total = 0;
	for (int i=0; i < data.size(); i++)
	    total += dataController.getHmVideo().get(data.get(i)).getNoOfLikes();
	return total / data.size();
    }
}
