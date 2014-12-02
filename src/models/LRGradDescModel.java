package models;

import controllers.dataControllers.dataController;
import controllers.dataControllers.FeatureController;
import utilities.Configuration;

import java.util.List;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 *
 * Logistic Regression using Gradient Descent
 */

public class LRGradDescModel extends genericModel {

    public LRGradDescModel(List<Object> trainData, List<Object> testData) {
        // Initialize the data
        this.trainData = trainData;
        this.testData = testData;

        // Initialize the parameters
        modelParams = new FeatureController();
    }

    public void run() {
        try {
            //System.out.println("Training...");
            train();

            //System.out.println("Testing...");
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
            FeatureController gradient = new FeatureController();

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
                    FeatureController X_ij = new FeatureController();


                    // 1. Numeric features
                    // 1.0 Intercept weight w_0
                    X_ij.getHmNumericFeatures().put(0, 1.0);

                    /*
                    // 1.1 No of likes
                    X_ij.getHmNumericFeatures().put(1, 1.0*(dataController.getHmVideo().get(item1).getNoOfLikes() /
                                                            dataController.getHmVideo().get(item2).getNoOfLikes()));
                    // 1.2 No of dislikes
                    X_ij.getHmNumericFeatures().put(2, 1.0*(dataController.getHmVideo().get(item1).getNoOfDislikes() /
                                                            dataController.getHmVideo().get(item2).getNoOfDislikes()));
                    */

                    // 1.3 Video Length In Seconds
                    X_ij.getHmNumericFeatures().put(3, 1.0 * dataController.getHmVideo().get(item1).getVideoLengthInSeconds() /
                                                             dataController.getHmVideo().get(item2).getVideoLengthInSeconds());

                    // 2. Bag of Words (from Title only)
                    String[] titleArr = dataController.getHmVideo().get(item1).getTitle().split(",");
                    for (String str:titleArr)
                    {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf++;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }

                    int titleLength1 = titleArr.length;

                    titleArr = dataController.getHmVideo().get(item2).getTitle().split(",");
                    for (String str:titleArr)
                    {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf--;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }

                    int titleLength2 = titleArr.length;
                    X_ij.getHmNumericFeatures().put(4, 1.0 * titleLength1 / titleLength2);

                    // 3. Category
                    /*
                    Double tf = X_ij.getHmCategoryFeatures().get(dataController.getHmVideo().get(item1).getCategory());
                    if (tf == null)
                        tf = 0.0;
                    tf++;
                    X_ij.getHmCategoryFeatures().put(dataController.getHmVideo().get(item1).getCategory(), tf);

                    tf = X_ij.getHmCategoryFeatures().get(dataController.getHmVideo().get(item2).getCategory());
                    if (tf == null)
                        tf = 0.0;
                    tf--;
                    X_ij.getHmCategoryFeatures().put(dataController.getHmVideo().get(item2).getCategory(), tf);
                    */

                    // 4. Uploader ID
                    Double tf = X_ij.getHmChannelIDFeatures().get(dataController.getHmVideo().get(item1).getChannelID());
                    if (tf == null)
                        tf = 0.0;
                    tf++;
                    X_ij.getHmChannelIDFeatures().put(dataController.getHmVideo().get(item1).getChannelID(), tf);

                    tf = X_ij.getHmChannelIDFeatures().get(dataController.getHmVideo().get(item2).getChannelID());
                    if (tf == null)
                        tf = 0.0;
                    tf--;
                    X_ij.getHmChannelIDFeatures().put(dataController.getHmVideo().get(item2).getChannelID(), tf);

                    // Compute the <w, X>
                    Double w = 0.0;
                    Double exponential = 0.0;

                    for (Integer idF:X_ij.getHmNumericFeatures().keySet())
                    {
                        w = modelParams.getOrInitFeature(0, idF);
                        exponential += w * X_ij.getOrInitFeature(0, idF);
                    }

                    for (int featureType=1; featureType<4; featureType++)
                    {
                        for (String key:X_ij.getStringFeatures(featureType).keySet())
                        {
                            w = modelParams.getOrInitFeature(featureType, key);
                            exponential += w * X_ij.getOrInitFeature(featureType, key);
                        }
                    }

                    exponential = Math.exp(exponential);
                    exponential = - exponential / (1 + exponential);

                    //System.out.println(exponential);

                    // Update the gradient
                    for (Integer idF:X_ij.getHmNumericFeatures().keySet())
                    {
                        w = gradient.getOrInitFeature(0, idF);
                        w += X_ij.getOrInitFeature(0, idF) * exponential;
                        gradient.setFeature(0, idF, w);
                    }

                    for (int featureType=1; featureType<4; featureType++)
                    {
                        for (String key:X_ij.getStringFeatures(featureType).keySet())
                        {
                            w = gradient.getOrInitFeature(featureType, key);
                            w += X_ij.getOrInitFeature(featureType, key) * exponential;
                            gradient.setFeature(featureType, key, w);
                        }
                    }
                }

            // Update the parameter
            double w_d = 0.0;
            for (Integer idF:modelParams.getHmNumericFeatures().keySet())
            {
                w_d = modelParams.getOrInitFeature(0, idF);
                w_d += Configuration.getInstance().getEta() * gradient.getOrInitFeature(0, idF);
                modelParams.setFeature(0, idF, w_d);
                //System.out.print(w_d + " ");
            }

            //System.out.println();

            for (int featureType=1; featureType<4; featureType++)
            {
                for (String key:modelParams.getStringFeatures(featureType).keySet())
                {
                    w_d = modelParams.getOrInitFeature(featureType, key);
                    w_d += Configuration.getInstance().getEta() * gradient.getOrInitFeature(featureType, key);
                    modelParams.setFeature(featureType, key, w_d);
                    //System.out.print(w_d + " ");
                }
            }

            //System.out.println();
        }
    }

    void test() throws Exception {
        Object[] arr = testData.toArray();

        int correct = 0;
        int count = 0;

        // For all pairs (i, j) such that views_i > views_j
        for (int idI1=0; idI1 < arr.length-1; idI1++)
            for (int idI2=idI1+1; idI2 < arr.length; idI2++) {
                Object item1 = arr[idI1];
                Object item2 = arr[idI2];

                count++;

                // Create representative feature vector
                FeatureController X_ij = new FeatureController();

                // 1. Numeric features
                // 1.1 No of likes
                X_ij.getHmNumericFeatures().put(0, 1.0*(dataController.getHmVideo().get(item1).getNoOfLikes()/
                        dataController.getHmVideo().get(item2).getNoOfLikes()));
                // 1.2 No of dislikes
                X_ij.getHmNumericFeatures().put(1, 1.0*(dataController.getHmVideo().get(item1).getNoOfDislikes()/
                        dataController.getHmVideo().get(item2).getNoOfDislikes()));

                // 2. Bag of Words (from Title only)
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
                Double tf = X_ij.getHmCategoryFeatures().get(dataController.getHmVideo().get(item1).getCategory());
                if (tf == null)
                    tf = 0.0;
                tf++;
                X_ij.getHmCategoryFeatures().put(dataController.getHmVideo().get(item1).getCategory(), tf);

                tf = X_ij.getHmCategoryFeatures().get(dataController.getHmVideo().get(item2).getCategory());
                if (tf == null)
                    tf = 0.0;
                tf--;
                X_ij.getHmCategoryFeatures().put(dataController.getHmVideo().get(item2).getCategory(), tf);

                // 4. Uploader ID
                tf = X_ij.getHmChannelIDFeatures().get(dataController.getHmVideo().get(item1).getChannelID());
                if (tf == null)
                    tf = 0.0;
                tf++;
                X_ij.getHmChannelIDFeatures().put(dataController.getHmVideo().get(item1).getChannelID(), tf);

                tf = X_ij.getHmChannelIDFeatures().get(dataController.getHmVideo().get(item2).getChannelID());
                if (tf == null)
                    tf = 0.0;
                tf--;
                X_ij.getHmChannelIDFeatures().put(dataController.getHmVideo().get(item2).getChannelID(), tf);

                // Compute the <w, X>
                Double w = 0.0;
                Double exponential = 0.0;

                for (Integer idF:X_ij.getHmNumericFeatures().keySet())
                {
                    w = modelParams.getOrInitFeature(0, idF);
                    exponential += w * X_ij.getOrInitFeature(0, idF);
                }

                for (int featureType=1; featureType<4; featureType++)
                {
                    for (String key:X_ij.getStringFeatures(featureType).keySet())
                    {
                        w = modelParams.getOrInitFeature(featureType, key);
                        exponential += w * X_ij.getOrInitFeature(featureType, key);
                    }
                }

                exponential = Math.exp(exponential);

                double prob_1 = 1 / (1 + exponential);
                double prob_0 = 1 - prob_1;

                if ((dataController.getHmVideo().get(item1).getViewCount() -
                        dataController.getHmVideo().get(item2).getViewCount())*(prob_1 - prob_0) >= 0.0) {
                    correct++;
                    /*
                    System.out.println(dataController.getHmVideo().get(item1).getTitle()+" "
                                        +dataController.getHmVideo().get(item1).getViewCount()+" "
                                        +dataController.getHmVideo().get(item2).getTitle()+" "
                                        +dataController.getHmVideo().get(item2).getViewCount()+" "
                                        +prob_1+" "
                                        +prob_0+" ");
                    */
                }

            }
        double errorRatio = correct*1.0 / count;
        //System.out.println("Error ratio: "+errorRatio+" ("+correct+" over "+count+").");
        bw.write(errorRatio+" ("+correct+"/"+count+") ");
    }
}
