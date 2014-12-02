package models;

import controllers.dataControllers.dataController;
import controllers.dataControllers.FeatureController;
import objects.youtubeObjects.youtubeVideo;
import utilities.Configuration;
import utilities.CrossValidation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create on 1/12/14
 *
 * @author Loc Do
 */
public class baggingModel extends genericModel {

    public void run() throws java.io.IOException, Exception {
        HashMap<Long, List<String>> hmVideoBins = new HashMap<Long, List<String>>();

        HashMap<String, youtubeVideo> hmVideo = dataController.getHmVideo();

        for (String vID:hmVideo.keySet()) {
            youtubeVideo video = hmVideo.get(vID);

            List <String> lstBin = hmVideoBins.get(video.getHowLongAgoUploaded());

            if (lstBin == null)
                lstBin = new ArrayList<String>();

            lstBin.add(vID);
            hmVideoBins.put(video.getHowLongAgoUploaded(), lstBin);
        }

        HashMap<Long, List<Double>> results = new HashMap<Long, List<Double>>();

        for (int fold=1; fold<6; fold++) {
            System.out.println("Training with fold "+fold);

            //List<featureController> ensemble = new ArrayList<featureController>();
            HashMap<Long, FeatureController> ensemble = new HashMap<Long, FeatureController>();

            CrossValidation cv = new CrossValidation(Configuration.getInstance().getMaxFold());

            System.out.println("Start with training ensemble...");
            for (Long age:hmVideoBins.keySet()) {
                List<String> lstVideos = hmVideoBins.get(age);
                if (lstVideos.size()>=20 && lstVideos.size()<=100) {
                    cv.loadData(lstVideos);

                    List<Object> train = cv.getTrainingDataInFold(fold);
                    //List<Object> test = cv.getTestingDataInFold(fold);

                    genericModel model = new LRGradDescModel(train, null);
                    //genericModel model = new LRAdaGradModel();
                    model.setBw(bw);
                    model.train();

                    ensemble.put(age, model.getModelParams());
                }
                    //System.out.println(age + "\t" + hmVideoBins.get(age).size());
                    //if (count==1) break;
            }

            System.out.println("Start with testing ensemble...");
            for (Long age:hmVideoBins.keySet()) {
                List<String> lstVideos = hmVideoBins.get(age);
                if (lstVideos.size()>=20 && lstVideos.size()<=100) {
                    cv.loadData(lstVideos);

                    List<Object> test = cv.getTestingDataInFold(fold);

                    List<Double> resultFold = results.get(age);
                    if (resultFold == null)
                        resultFold = new ArrayList<Double>();

                    resultFold.add(test(ensemble, test, age));

                    results.put(age, resultFold);
                }
                //System.out.println(age + "\t" + hmVideoBins.get(age).size());
                //if (count==1) break;
            }
        }

        System.out.println("Writing the results...");
        for (Long age:results.keySet()) {
            bw.write("Bin "+age+" ");
            for (Double error:results.get(age)) {
                bw.write(error+" ");
            }
            bw.write("\n");
            bw.flush();
        }

    }

    public double test(HashMap<Long, FeatureController> ensemble, List<Object> testData, Long selectedAge) throws Exception {
        Object[] arr = testData.toArray();

        int correct = 0;
        int count = 0;

        // For all pairs (i, j) such that views_i > views_j
        for (int idI1=0; idI1 < arr.length-1; idI1++)
            for (int idI2=idI1+1; idI2 < arr.length; idI2++)
            {
                double label_1 = 0;
                double label_0 = 0;

                Object item1 = arr[idI1];
                Object item2 = arr[idI2];

                count++;

                //for (featureController weight:ensemble) {
                for (Long age:ensemble.keySet()) {
                    FeatureController weight = ensemble.get(selectedAge);

                    // Create representative feature vector
                    FeatureController X_ij = new FeatureController();

                    // 0. Intercept
                    X_ij.getHmNumericFeatures().put(0, 1.0);

                    // 1. Numeric features
                    double ratioOfLikesOfV1 = 1.0 * dataController.getHmVideo().get(item1).getNoOfLikes();
                    double ratioOfDislikesOfV1 = 1.0 * dataController.getHmVideo().get(item1).getNoOfDislikes();

                    double ratioOfLikesOfV2 = 1.0 * dataController.getHmVideo().get(item2).getNoOfLikes();
                    double ratioOfDislikesOfV2 = 1.0 * dataController.getHmVideo().get(item2).getNoOfDislikes();

                    //X_ij.getHmNumericFeatures().put(1, (ratioOfDislikesOfV1 + ratioOfLikesOfV1) /
                    //                                   (ratioOfDislikesOfV2 + ratioOfLikesOfV2));

                    //double coeff = (ratioOfDislikesOfV1 + ratioOfLikesOfV1) /
                    //                (ratioOfDislikesOfV2 + ratioOfLikesOfV2);

                    ratioOfLikesOfV1 = ratioOfLikesOfV1 / (ratioOfLikesOfV1 + ratioOfDislikesOfV1);
                    ratioOfDislikesOfV1 = 1 - ratioOfLikesOfV1;

                    ratioOfLikesOfV2 = ratioOfLikesOfV2 / (ratioOfLikesOfV2 + ratioOfDislikesOfV2);
                    ratioOfDislikesOfV2 = 1 - ratioOfLikesOfV2;

                    // 1.1 No of likes
                    //X_ij.getHmNumericFeatures().put(1, coeff*(ratioOfLikesOfV1 - ratioOfLikesOfV2));

                    // 1.2 No of dislikes
                    //X_ij.getHmNumericFeatures().put(2, Math.log(ratioOfDislikesOfV1) - Math.log(ratioOfDislikesOfV2));

                    // 1.3 Video Length
                    X_ij.getHmNumericFeatures().put(3, (1.0) * dataController.getHmVideo().get(item1).getVideoLengthInSeconds() /
                                                    dataController.getHmVideo().get(item2).getVideoLengthInSeconds() );

                    // 2. Bag of Words (from Title only)
                    int length1 = 0;
                    int length2 = 0;

                    String[] titleArr = dataController.getHmVideo().get(item1).getTitle().split(",");
                    for (String str : titleArr) {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf++;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }
                    length1 = titleArr.length;

                    titleArr = dataController.getHmVideo().get(item2).getTitle().split(",");
                    for (String str : titleArr) {
                        Double tf = X_ij.getHmBoWFeatures().get(str);
                        if (tf == null)
                            tf = 0.0;
                        tf--;
                        X_ij.getHmBoWFeatures().put(str, tf);
                    }
                    length2 = titleArr.length;

                    X_ij.getHmNumericFeatures().put(4, (1.0) * length1 / length2);

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
                    Double tf = 0.0;

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

                    for (Integer idF : X_ij.getHmNumericFeatures().keySet()) {
                        w = weight.getOrInitFeature(0, idF);
                        exponential += w * X_ij.getOrInitFeature(0, idF);
                    }

                    for (int featureType = 1; featureType < 4; featureType++) {
                        for (String key : X_ij.getStringFeatures(featureType).keySet()) {
                            w = weight.getOrInitFeature(featureType, key);
                            exponential += w * X_ij.getOrInitFeature(featureType, key);
                        }
                    }

                    exponential = Math.exp(exponential);

                    double prob_1 = 1 / (1 + exponential);
                    double prob_0 = 1 - prob_1;

                    if (Double.isInfinite(exponential) || Double.isNaN(exponential))
                        System.out.println("Found invalid value of exponential...");

                    if (age == selectedAge) {
                        if ((prob_1 - prob_0) > 0.0)
                            //label_1++;
                            label_1 += Math.exp(-Math.abs(age - selectedAge));
                        else
                            //label_0++;
                            label_0 += Math.exp(-Math.abs(age - selectedAge));
                    }
                }

                //System.out.println("Stop");
                if ((dataController.getHmVideo().get(item1).getViewCount() -
                        dataController.getHmVideo().get(item2).getViewCount()) * (label_1 - label_0) >= 0.0) {
                    correct++;
                }
            }

        double errorRatio = correct*1.0 / count;
        //System.out.println(errorRatio);
        return errorRatio;
    }
}
