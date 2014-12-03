package models;

import controllers.dataControllers.FeatureController;
import controllers.dataControllers.dataController;
import objects.youtubeObjects.youtubeVideo;
import utilities.Configuration;

import java.util.List;

/**
 * Create on 2/12/14
 *
 * @author Loc Do
 */
public class LRStoGradAscModel extends genericModel {

    public LRStoGradAscModel(List<Object> trainData, List<Object> testData) {
        // Initialize the data
        this.trainData = trainData;
        this.testData = testData;

        // Initialize the parameters
        modelParams = new FeatureController();
    }

    @Override
    public void run(List<Object> trainData, List<Object> testData, String whereSaveModel) {
        try {
            //System.out.println("Training...");
            train();

            //System.out.println("Testing (on training data)...");
            test(false);

            //System.out.println("Testing (on testing data)...");
            test(true);

            if (!whereSaveModel.isEmpty())
                output(whereSaveModel);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void test(boolean onTestData) throws Exception {
        // HAS NOT IMPLEMENTED YET
    }

    @Override
    public void output(String filename) {
        modelParams.output(filename);
    }

    public void run() {
        try {
            //System.out.println("Training...");
            train();

            //System.out.println("Testing...");
            test(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void train() throws Exception {
        Object[] arr = trainData.toArray();
        int labelType = Configuration.getInstance().getLabelType();

        for (int idLoop = 0; idLoop < Configuration.getInstance().getNoOfIterations();
             idLoop++)
        {
            // For all pairs (i, j) such that views_i > views_j
            for (int idI1=0; idI1 < arr.length-1; idI1++)
                for (int idI2=idI1+1; idI2 < arr.length; idI2++)
                {
                    Object item1 = arr[idI1];
                    Object item2 = arr[idI2];

                    // Create representative feature vector
                    youtubeVideo v1 = dataController.getHmVideo().get(item1);
                    youtubeVideo v2 = dataController.getHmVideo().get(item2);

                    FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1, v2);

                    /*
                    // 1. Numeric features
                    // 1.0 Intercept weight w_0
                    X_ij.getHmNumericFeatures().put(0, 1.0);

                    // 1.1 No of likes
                    X_ij.getHmNumericFeatures().put(1, 1.0*(dataController.getHmVideo().get(item1).getNoOfLikes() /
                                                            dataController.getHmVideo().get(item2).getNoOfLikes()));
                    // 1.2 No of dislikes
                    X_ij.getHmNumericFeatures().put(2, 1.0*(dataController.getHmVideo().get(item1).getNoOfDislikes() /
                                                            dataController.getHmVideo().get(item2).getNoOfDislikes()));

                    double ratioOfLike1 = dataController.getHmVideo().get(item1).getNoOfLikes();
                    double ratioOfDislike1 = dataController.getHmVideo().get(item1).getNoOfDislikes();

                    ratioOfLike1 /= (ratioOfLike1 + ratioOfDislike1);

                    double ratioOfLike2 = dataController.getHmVideo().get(item2).getNoOfLikes();
                    double ratioOfDislike2 = dataController.getHmVideo().get(item2).getNoOfDislikes();

                    ratioOfLike2 /= (ratioOfLike2 + ratioOfDislike2);

                    //X_ij.getHmNumericFeatures().put(1, ratioOfLike1);

                    //X_ij.getHmNumericFeatures().put(2, ratioOfLike2);

                    // 1.3 Video Length In Seconds
                    //X_ij.getHmNumericFeatures().put(3, 1.0 * dataController.getHmVideo().get(item1).getVideoLengthInSeconds() /
                    //                                         dataController.getHmVideo().get(item2).getVideoLengthInSeconds());

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
                    //X_ij.getHmNumericFeatures().put(4, 1.0 * titleLength1 / titleLength2);

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
                    */

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
                    exponential = exponential / (1 + exponential);

                    //System.out.println(exponential);

                    // Update the parameters
                    double w_d = 0.0;
                    for (Integer idF:modelParams.getHmNumericFeatures().keySet())
                    {
                        w_d = modelParams.getOrInitFeature(0, idF);

                        if (dataController.getHmVideo().get(item1).getRatioOfLikeDislike() >
                                dataController.getHmVideo().get(item2).getRatioOfLikeDislike())
                            w_d += Configuration.getInstance().getEta() * // learning rate
                                    X_ij.getOrInitFeature(0, idF) * (- exponential) // gradient
                                    - Configuration.getInstance().getLambda() * w_d; // regularization
                        else
                            w_d += Configuration.getInstance().getEta() * // learning rate
                                    X_ij.getOrInitFeature(0, idF) * (1 - exponential) // gradient
                                    - Configuration.getInstance().getLambda() * w_d; // regularization

                        modelParams.setFeature(0, idF, w_d);
                        //System.out.print(w_d + " ");
                    }

                    //System.out.println();

                    for (int featureType=1; featureType<4; featureType++)
                    {
                        for (String key:modelParams.getStringFeatures(featureType).keySet())
                        {
                            w_d = modelParams.getOrInitFeature(featureType, key);

                            if (dataController.getHmVideo().get(item1).getRatioOfLikeDislike() >
                                    dataController.getHmVideo().get(item2).getRatioOfLikeDislike())
                                w_d += Configuration.getInstance().getEta() * // learning rate
                                       X_ij.getOrInitFeature(featureType, key) * (- exponential)
                                       - Configuration.getInstance().getLambda() * w_d;
                            else
                                w_d += Configuration.getInstance().getEta() * // learning rate
                                        X_ij.getOrInitFeature(featureType, key) * (1 - exponential)
                                        - Configuration.getInstance().getLambda() * w_d;
                            modelParams.setFeature(featureType, key, w_d);
                            //System.out.print(w_d + " ");
                        }
                    }
                }

            //System.out.println();
        }
    }
}
