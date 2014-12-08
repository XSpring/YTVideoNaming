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
 *
 * Logistic Regression using Gradient Descent
 */

public class LRGradDescModel extends GenericModel {
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
                    FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1,v2, 0);
		    double innerProd = FeatureController.getInnerProduct(modelParams, X_ij);
                    double exponential = Math.exp(innerProd);
                    exponential = - exponential / (1 + exponential);
		    gradient.addWithScaling(X_ij, exponential);
                }
	    }
	    modelParams.addWithScaling(gradient, Configuration.getInstance().getEta());
        }
    }

    @Override
    protected void test(boolean onTestData) {
	java.util.List<Object> data = onTestData ? testData : trainData;
	long count = data.size() * data.size() / 2;
        int correct = 0;
        for (int idI1=0; idI1 < trainData.size()-1; idI1++) {
	    for (int idI2=idI1+1; idI2 < trainData.size(); idI2++) {
		youtubeVideo v1 = dataController.getHmVideo().get(trainData.get(idI1));
		youtubeVideo v2 = dataController.getHmVideo().get(trainData.get(idI2));
		boolean firstIsMorePopular_predicted = predictIsFirstArgMorePopular(v1,v2);
		boolean firstIsMorePopular_observed = v1.getViewCount() > v2.getViewCount();
		if (firstIsMorePopular_predicted == firstIsMorePopular_observed)
		    correct++;
	    }
	}
        //output the results
	long bin = dataController.getHmVideo().get(data.get(0)).getHowLongAgoUploaded();
	double sqrtErrSq = 0.0;	    //making something up; that field doesn't matter here
	outputResultsLine(onTestData, bin, sqrtErrSq, correct, count);
    }
    
    private boolean predictIsFirstArgMorePopular(youtubeVideo v1, youtubeVideo v2) {
	FeatureController X_ij = FeatureController.getFeatureControllerFromVids_0(v1,v2, 0);
	double innerProd = FeatureController.getInnerProduct(modelParams, X_ij);
	double exponential = Math.exp(innerProd);
	double prob_1 = 1 / (1 + exponential);
	double prob_0 = 1 - prob_1;
	return prob_0 > prob_1;
    }
}
