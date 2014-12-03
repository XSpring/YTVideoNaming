package models;

import controllers.dataControllers.dataController;
import controllers.dataControllers.FeatureController;
import utilities.Configuration;
import objects.youtubeObjects.*;

import java.util.List;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 */

//this uses a different train() method, but otherwise matches LRGradDescModel
public class LRAdaGradModel extends LRGradDescModel {
    private FeatureController stackedGradients = null;

    @Override
    protected void train() {
	for (int iteration = 0; iteration < Configuration.getInstance().getNoOfIterations(); iteration++) {
            FeatureController gradient = new FeatureController();
            for (int idI1=0; idI1 < trainData.size()-1; idI1++) {
                for (int idI2=idI1+1; idI2 < trainData.size(); idI2++) {
		    // For all pairs (i, j) such that views_i > views_j
		    youtubeVideo v1 = dataController.getHmVideo().get(trainData.get(idI1));
		    youtubeVideo v2 = dataController.getHmVideo().get(trainData.get(idI2));
		    if (v1.getViewCount() < v2.getViewCount()) {    //swap them
			youtubeVideo temp = v2; v2 = v1; v1 = temp;
		    }
                    FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1,v2);
		    double innerProd = FeatureController.getInnerProduct(modelParams, X_ij);
                    double exponential = Math.exp(innerProd);
                    exponential = - exponential / (1 + exponential);
		    gradient.addWithScaling(X_ij, exponential);
                }
	    }
	    
	    // Update the parameter
            double w_d = 0.0;
            double learningRateCoeff = 0.0;

            for (Integer idF:modelParams.getHmNumericFeatures().keySet()) {
                w_d = modelParams.getOrInitFeature(0, idF);
                learningRateCoeff = stackedGradients.getOrInitFeature(0, idF);
                if (learningRateCoeff == 0.0)
                    w_d +=  Configuration.getInstance().getEta() * gradient.getOrInitFeature(0, idF);
                else
                    w_d += ( Configuration.getInstance().getEta() / Math.sqrt(learningRateCoeff) ) * gradient.getOrInitFeature(0, idF);
                learningRateCoeff += gradient.getOrInitFeature(0, idF) * gradient.getOrInitFeature(0, idF);
                modelParams.setFeature(0, idF, w_d);
                stackedGradients.setFeature(0, idF, learningRateCoeff);
            }

	    for (int featureType=1; featureType<4; featureType++) {
                for (String key:modelParams.getStringFeatures(featureType).keySet()) {
                    w_d = modelParams.getOrInitFeature(featureType, key);
                    learningRateCoeff = stackedGradients.getOrInitFeature(featureType, key);
                    if (learningRateCoeff == 0.0)
                        w_d += Configuration.getInstance().getEta() * gradient.getOrInitFeature(featureType, key);
                    else
                        w_d += ( Configuration.getInstance().getEta() / Math.sqrt(learningRateCoeff) ) * gradient.getOrInitFeature(featureType, key);
                    learningRateCoeff += gradient.getOrInitFeature(featureType, key) * gradient.getOrInitFeature(featureType, key);
                    modelParams.setFeature(featureType, key, w_d);
                    stackedGradients.setFeature(featureType, key, learningRateCoeff);
                }
            }
        }
    }
}
