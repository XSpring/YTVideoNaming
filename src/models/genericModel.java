package models;

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

    public genericModel() {

    }

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public void run(List<Object> train, List<Object> test) {

    }

    void train() throws Exception {

    }

    void test() throws Exception {

    }
}
