package controllers.modelControllers;

import controllers.dataControllers.dataController;
import models.LRGradDescModel;
import models.genericModel;
import utilities.Configuration;
import utilities.CrossValidation;

import java.io.BufferedWriter;
import java.util.List;

/**
 * Create on 28/11/14
 *
 * @author Loc Do
 */
public class modelController {

    CrossValidation cv = new CrossValidation(Configuration.getInstance().getMaxFold());

    BufferedWriter bw = null;

    public void loadData(List<String> lstVideos) {
        cv.loadData(lstVideos);
    }

    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public void run() throws Exception {

        for (int id = 1; id < 6; id++) {
            List<Object> train = cv.getTrainingDataInFold(id);
            List<Object> test = cv.getTestingDataInFold(id);

            /*
            System.out.println("Training data...");
            for (Object obj:train)
                System.out.println(obj+" "+dataController.getHmVideo().get(obj).getViewCount());

            System.out.println("Testing data...");
            for (Object obj:test)
                System.out.println(obj+" "+dataController.getHmVideo().get(obj).getViewCount());
            */

            genericModel model = new LRGradDescModel(train, test);
            //genericModel model = new LRAdaGradModel();
            model.setBw(bw);
            model.run(train, test);
        }
        bw.write("\n");
        bw.flush();
        System.out.println("Done with training/testing data...");
    }

    public void testDataValidity() {
        System.out.println("Test overlapping between training and testing set at each fold.");

        for (int id=1; id<6; id++) {
            List<Object> train = cv.getTrainingDataInFold(id);
            List<Object> test = cv.getTestingDataInFold(id);

            //
            for (Object obj:train)
                if (test.contains(obj)) {
                    System.out.println("Found duplicate entries in training and testing set");
                }

            for (Object obj:train)
                if (dataController.getHmVideo().get(obj) == null)
                    System.out.println("Found NULL objects.");

            for (Object obj:test)
                if (dataController.getHmVideo().get(obj) == null)
                    System.out.println("Found NULL objects.");
        }

        System.out.println("Done.");
    }
}
