package models;

import controllers.dataControllers.dataController;
import controllers.dataControllers.featureController;
import utilities.Configuration;

import java.util.List;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 *
 * Logistic Regression using Gradient Descent
 */

public class LRGradDescModel extends model {

    featureController modelParams = null;

    public void run(List<Object> trainData, List<Object> testData) {
        // Initialize the data
        this.trainData = trainData;
        this.testData = testData;

        // Initialize the parameters
        modelParams = new featureController();

        try {
            train();
            test();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void train() throws Exception {
        Object[] arr = trainData.toArray();

        for (int idLoop = 0; idLoop < Configuration.getInstance().getNoOfIterations();
                 idLoop++)
        {

            // For all pairs (i, j) such that views_i > views_j
            for (int idI1=0; idI1 < arr.length-1; idI1++)
                for (int idI2=idI1+1; idI2 < arr.length; idI2++)
                {
                    Object item1 = arr[idI1];
                    Object item2 = arr[idI2];

                    if (dataController.getHmVideo().get(item1).getViewCount() <
                            dataController.getHmVideo().get(item2).getViewCount())
                    {
                        item1 = arr[idI2];
                        item2 = arr[idI1];
                    }

                    // Create representative feature vector
                    featureController X_ij = new featureController();

                    // 1. Numeric features
                    // 1.1 No of likes
                    X_ij.getHmNumericFeatures().put(0, 1.0*(dataController.getHmVideo().get(item1).getNoOfLikes()-
                                                            dataController.getHmVideo().get(item2).getNoOfLikes()));
                    // 1.2 No of dislikes
                    X_ij.getHmNumericFeatures().put(1, 1.0*(dataController.getHmVideo().get(item1).getNoOfDislikes()-
                                                            dataController.getHmVideo().get(item2).getNoOfDislikes()));

                    // 2. Bag of Words
                    String[] titleArr = dataController.getHmVideo().get(item1).getTitle().split(",");
                    for (String str:titleArr)
                    {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf++;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }

                    titleArr = dataController.getHmVideo().get(item2).getTitle().split(",");
                    for (String str:titleArr)
                    {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf--;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }

                    // 3. Category
                    // 4. Uploader ID
                }
        }
    }

    void test() throws Exception {

    }
}
