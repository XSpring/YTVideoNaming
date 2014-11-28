package controllers.modelControllers;

import controllers.dataControllers.dataController;
import utilities.CrossValidation;

import java.util.List;

/**
 * Create on 28/11/14
 *
 * @author Loc Do
 */
public class modelController {

    CrossValidation cv = new CrossValidation();

    public void loadData(List<String> lstVideos) {
        cv.loadData(lstVideos);
    }

    public void run() {
        System.out.println("Test overlapping between training and testing set at each fold.");

        for (int id=1; id<6; id++) {
            List<Object> train = cv.getTrainingDataInFold(id);
            List<Object> test = cv.getTestingDataInFold(id);

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
