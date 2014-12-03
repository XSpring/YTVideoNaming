package models;

import controllers.dataControllers.FeatureController;
import objects.youtubeObjects.youtubeVideo;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 1/11/14.
 * @author Loc Do
 */

public abstract class genericModel {

    List<Object> trainData = null;
    List<Object> testData = null;

    BufferedWriter bw = null;

    FeatureController modelParams = null;

    public genericModel() {

    }

    public FeatureController getModelParams() {
        return modelParams;
    }

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public abstract void run(List<Object> train, List<Object> test, String whereSaveModel);
    abstract void train() throws Exception;
    abstract void test(boolean onTestData) throws Exception;
    public abstract void output(String filename);
}
