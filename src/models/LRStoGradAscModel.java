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

public class LRStoGradAscModel extends GenericModel {

    public LRStoGradAscModel(List<Object> trainData, List<Object> testData) {
        super(trainData, testData);
    }

    @Override
    protected void train() {
        for (int iteration = 0; iteration < Configuration.getInstance().getNoOfIterations(); iteration++) {
	    for (int idI1=0; idI1 < trainData.size()-1; idI1++) {
                for (int idI2=idI1+1; idI2 < trainData.size(); idI2++) {
		    youtubeVideo v1 = dataController.getHmVideo().get(trainData.get(idI1));
		    youtubeVideo v2 = dataController.getHmVideo().get(trainData.get(idI2));
		    FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1, v2, 0);
		    double innerProd = FeatureController.getInnerProduct(X_ij, modelParams);
                    //System.out.println(innerProd);
                    double exponential = Math.exp(innerProd);
                    exponential = exponential / (1 + exponential);
                    //System.out.println(exponential);

                    if (Double.isNaN(exponential))
                        System.out.println("NaN found.");

                    double ratio = 1.0;
                    if (v1.getViewCount() > v2.getViewCount())
                        ratio = Math.log(v1.getViewCount()*1.0) / Math.log(v2.getViewCount());
                    else
                        ratio = Math.log(v2.getViewCount()*1.0) / Math.log(v1.getViewCount());

                    if (!Configuration.getInstance().isGrad())
                        ratio = 1.0;

                    // Update the parameters
                    for (Integer idF:modelParams.getHmNumericFeatures().keySet()) {
                        double w_d = modelParams.getOrInitFeature(0, idF);
                        //System.out.println(w_d+"\t"+);
                        if (v1.getViewCount() > v2.getViewCount()) {
                            w_d += Configuration.getInstance().getEta() * // learning rate
                                    ratio * (
                                    X_ij.getOrInitFeature(0, idF) * (- exponential) // gradient
                                    - Configuration.getInstance().getLambda() * w_d); // regularization
			} else {
                            w_d += Configuration.getInstance().getEta() * // learning rate
                                    ratio * (
                                    X_ij.getOrInitFeature(0, idF) * (1 - exponential) // gradient
                                    - Configuration.getInstance().getLambda() * w_d); // regularization
			}
                        modelParams.setFeature(0, idF, w_d);
                    }
                    for (int featureType=1; featureType<4; featureType++) {
                        for (String key:modelParams.getStringFeatures(featureType).keySet()) {
                            double w_d = modelParams.getOrInitFeature(featureType, key);
                            if (v1.getViewCount() > v2.getViewCount()) {
                                w_d += Configuration.getInstance().getEta() * // learning rate
                                        ratio * (
                                       X_ij.getOrInitFeature(featureType, key) * (- exponential)
                                       - Configuration.getInstance().getLambda() * w_d);
			    } else {
                                w_d += Configuration.getInstance().getEta() * // learning rate
                                        ratio * (
                                        X_ij.getOrInitFeature(featureType, key) * (1 - exponential)
                                        - Configuration.getInstance().getLambda() * w_d);
			    }
                            modelParams.setFeature(featureType, key, w_d);
                        }
                    }
                }
	    }
        }
    }
    
    @Override
    protected void test(boolean onTestData) {
        // HAS NOT IMPLEMENTED YET
    }
}
