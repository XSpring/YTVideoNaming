package models;

import java.io.BufferedWriter;
import java.util.List;

/**
 * Created on 1/11/14.
 * @author Loc Do
 */

public abstract class genericModel {

    List<Object> trainData = null;
    List<Object> testData = null;

    BufferedWriter bw = null;

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public abstract void run(List<Object> train, List<Object> test);
    abstract void train() throws Exception;
    abstract void test() throws Exception;
    public abstract void output(String filename);
}
