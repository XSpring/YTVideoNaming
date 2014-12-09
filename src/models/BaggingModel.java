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

public class BaggingModel extends GenericModel {

    @Override
    public void run(List<Object> trainData, List<Object> testData, String whereSaveModel) {
        // HAS NOT IMPLEMENTED YET
    }

    @Override
    protected void train() {
        // HAS NOT IMPLEMENTED YET
    }

    @Override
    protected void test(boolean onTestData) {
        // HAS NOT IMPLEMENTED YET
    }

    public void run() throws Exception {
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

        HashMap<Long, List<confusionMatrix>> results = new HashMap<Long, List<confusionMatrix>>();

        for (int fold=1; fold<6; fold++) {
            System.out.println("Training with fold " + fold);

            //List<featureController> ensemble = new ArrayList<featureController>();
            HashMap<Long, FeatureController> ensemble = new HashMap<Long, FeatureController>();

            CrossValidation cv = new CrossValidation(Configuration.getInstance().getMaxFold());

            System.out.println("Start with training ensemble...");
            for (Long age : hmVideoBins.keySet())
                if (age >= Configuration.getInstance().getMinAge() && age <= Configuration.getInstance().getMaxAge()) {
                    List<String> lstVideos = hmVideoBins.get(age);

                    if (lstVideos.size() >= Configuration.getInstance().getMinSize() &&
                            lstVideos.size() <= Configuration.getInstance().getMaxSize()) {

                        cv.loadData(lstVideos);

                        List<Object> train = cv.getTrainingDataInFold(fold);
                        //List<Object> test = cv.getTestingDataInFold(fold);


                        GenericModel model = new LRStoGradAscModel(train, null);
			            model.modelParams = new FeatureController();
			            model.setBw(bw);
                        model.train();

                        ensemble.put(age, model.getModelParams());
                    }
                    //System.out.println(age + "\t" + hmVideoBins.get(age).size());
                    //if (count==1) break;
                }

            System.out.println("Start with testing ensemble...");
            for (Long age : hmVideoBins.keySet()) {
                if (age >= Configuration.getInstance().getMinAge() && age <= Configuration.getInstance().getMaxAge()) {
                    List<String> lstVideos = hmVideoBins.get(age);

                    if (lstVideos.size() >= Configuration.getInstance().getMinSize() &&
                            lstVideos.size() <= Configuration.getInstance().getMaxSize()) {

                        //FeatureController weight = ensemble.get(age);
                        //weight.output("weight/fold"+fold+"age"+age+".txt");

                        cv.loadData(lstVideos);

                        List<Object> test = cv.getTestingDataInFold(fold);
                        //List<Object> test = cv.getTrainingDataInFold(fold);

                        List<confusionMatrix> resultFold = results.get(age);
                        if (resultFold == null)
                            resultFold = new ArrayList<confusionMatrix>();

                        resultFold.add(test(ensemble, test, age));

                        results.put(age, resultFold);
                    }

                    //System.out.println(age + "\t" + hmVideoBins.get(age).size());
                    //if (count==1) break;
                }
            }
        }

        System.out.println("Writing the results...");
        for (Long age:results.keySet()) {
            bw.write("Bin "+age+" ");

            double avgAcc = 0.0;
            double avgF1 = 0.0;
            int counter = 0;

            for (confusionMatrix result:results.get(age)) {
                bw.write(result.getAccuracy()+" "+result.getF1Score()+" ");
                counter++;
                avgAcc += result.getAccuracy();
                avgF1 += result.getF1Score();
            }

            avgAcc /= counter;
            avgF1 /= counter;

            bw.write(avgAcc+" "+avgF1);
            bw.write("\n");
            bw.flush();
        }
    }

    public confusionMatrix test(HashMap<Long, FeatureController> ensemble, List<Object> testData, Long selectedAge) throws Exception {
        Object[] arr = testData.toArray();

        int correct = 0;
        int count = 0;

        confusionMatrix result = new confusionMatrix();

        int labelType = Configuration.getInstance().getLabelType();

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

                    //if (age != selectedAge) continue;

                    FeatureController weight = ensemble.get(age);

                    // Create representative feature vector
                    youtubeVideo v1 = dataController.getHmVideo().get(item1);
                    youtubeVideo v2 = dataController.getHmVideo().get(item2);

                    FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1, v2, 1);

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

                    //System.out.println(exponential+" "+prob_0+" "+prob_1);

                    if (Double.isInfinite(exponential) || Double.isNaN(exponential))
                        System.out.println("Found invalid value of exponential...");

                    if ((prob_1 - prob_0) > 0.0)
                            //label_1++;
                            //label_1 += Math.exp(-Math.abs(age - selectedAge));
                            label_1 += 1.0 / (1.0 + Math.abs(age - selectedAge));
                        else
                            //label_0++;
                            //label_0 += Math.exp(-Math.abs(age - selectedAge));
                            label_0 += 1.0 / (1.0 + Math.abs(age - selectedAge));
                    //}
                }

                boolean trueLabel = false;
                boolean predictLabel = false;


                if (dataController.getHmVideo().get(item1).getViewCount() -
                        dataController.getHmVideo().get(item2).getViewCount() > 0.0)
                    trueLabel = true;

                if (label_1 - label_0 >= 0.0)
                    predictLabel = true;

                if (trueLabel && predictLabel)
                    result.tp++;

                if (!trueLabel && !predictLabel)
                    result.tn++;

                if (trueLabel && !predictLabel)
                    result.fn++;

                if (!trueLabel && predictLabel)
                    result.fp++;
            }

        return result;
    }

    class confusionMatrix {
        public int tp = 0;
        public int fp = 0;
        public int tn = 0;
        public int fn = 0;

        public int getTotal() {
            return tp + tn + fp + fn;
        }

        public double getAccuracy() {
            return 1.0*(tp + tn) / getTotal();
        }

        public double getPrecision() {
            return 1.0*tp/(tp+fp);
        }

        public double getRecall() {
            return 1.0*tp/(fn + tp);
        }

        public double getF1Score() {
            return 2*getPrecision()*getRecall()/(getPrecision()+getRecall());
        }
    }

}
