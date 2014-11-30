package controllers.dataControllers;

import java.util.HashMap;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 */
public class featureController {

    HashMap<Integer, Double> hmNumericFeatures = null;
    HashMap<String, Double> hmBoWFeatures = null;
    HashMap<String, Double> hmChannelIDFeatures = null;
    HashMap<String, Double> hmCategoryFeatures = null;

    public featureController() {
        hmNumericFeatures = new HashMap<Integer, Double>();
        hmBoWFeatures = new HashMap<String, Double>();
        hmChannelIDFeatures = new HashMap<String, Double>();
        hmCategoryFeatures = new HashMap<String, Double>();
    }

    public HashMap<Integer, Double> getHmNumericFeatures() {
        return hmNumericFeatures;
    }

    public HashMap<String, Double> getHmBoWFeatures() {
        return hmBoWFeatures;
    }

    public HashMap<String, Double> getHmChannelIDFeatures() {
        return hmChannelIDFeatures;
    }

    public HashMap<String, Double> getHmCategoryFeatures() {
        return hmCategoryFeatures;
    }
}
