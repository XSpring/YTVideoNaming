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

public abstract class genericModel {
    protected List<Object> trainData = null;
    protected List<Object> testData = null;
    protected BufferedWriter bw = null;
    protected FeatureController modelParams = null;
    
    protected abstract void train();
    protected abstract void test(boolean onTestData);

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
    
    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }
    
    protected void outputModelParams(String filename) {
	modelParams.output(filename);
    }
}
