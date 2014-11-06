package Predictors;
import java.util.HashMap;
import objects.youtubeObjects.*;
import java.util.List;
import utilities.Common;
import utilities.DatafileGrabber;

/**
 * @author Richardson
 */
public abstract class Predictor implements Cloneable {
    public abstract double predictNumViews(youtubeVideo vid);
    public abstract double predictPercentLikes(youtubeVideo vid);
    public abstract double predictPercentDislikes(youtubeVideo vid);
    public abstract double predictLogNumViews(youtubeVideo vid);
    public abstract void train(List<youtubeVideo> trainVids);
    
    public double getLossNumViews(List<youtubeVideo> testVids) {
	double loss = 0;
	for (youtubeVideo vid : testVids) {
	    double pred = predictNumViews(vid);
	    double observed = vid.getViewCount();
	    double diff = pred - observed;
	    loss += diff * diff;
	}
	return loss;
    }
    
    public double getPercentLikes(List<youtubeVideo> testVids) {
	double loss = 0;
	for (youtubeVideo vid : testVids) {
	    double pred = predictPercentLikes(vid);
	    double observed = vid.getNoOfLikes() / vid.getViewCount();
	    double diff = pred - observed;
	    loss += diff * diff;
	}
	return loss;
    }
    
    public double getPercentDislikes(List<youtubeVideo> testVids) {
	double loss = 0;
	for (youtubeVideo vid : testVids) {
	    double pred = predictPercentDislikes(vid);
	    double observed = vid.getNoOfDislikes() / vid.getViewCount();
	    double diff = pred - observed;
	    loss += diff * diff;
	}
	return loss;
    }
    
    public double getLossLogNumViews(List<youtubeVideo> testVids) {
	double loss = 0;
	for (youtubeVideo vid : testVids) {
	    double pred = predictLogNumViews(vid);
	    double observed = Math.log(vid.getViewCount());
	    double diff = pred - observed;
	    loss += diff * diff;
	}
	return loss;
    }
}
