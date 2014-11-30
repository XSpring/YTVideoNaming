package controllers.dataControllers;

import java.util.HashMap;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 */
public class featureController {

    HashMap<Integer, Double> hmNumericFeatures = null; // type = 0
    HashMap<String, Double> hmBoWFeatures = null; // type = 1
    HashMap<String, Double> hmChannelIDFeatures = null; // type = 2
    HashMap<String, Double> hmCategoryFeatures = null; // type = 3

    public featureController() {
        hmNumericFeatures = new HashMap<Integer, Double>();
        hmBoWFeatures = new HashMap<String, Double>();
        hmChannelIDFeatures = new HashMap<String, Double>();
        hmCategoryFeatures = new HashMap<String, Double>();
    }

    public Double getOrInitFeature(int featureType, Object key) {
        Double value = 0.0;
        switch (featureType) {
            case 0: value = hmNumericFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmNumericFeatures.put((Integer)key, value);
                    }
                    break;
            case 1: value = hmBoWFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmBoWFeatures.put((String)key, value);
                    }
                    break;
            case 2: value = hmChannelIDFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmChannelIDFeatures.put((String)key, value);
                    }
                    break;
            case 3: value = hmCategoryFeatures.get(key);
                    if (value == null) {
                        value = 0.0;
                        hmCategoryFeatures.put((String)key, value);
                    }
                    break;
        }
        return value;
    }

    public boolean setFeature(int featureType, Object key, Object value) {
        try {
            switch (featureType) {
                case 0: hmNumericFeatures.put((Integer) key, (Double) value);
                        break;
                case 1: hmBoWFeatures.put((String) key, (Double) value);
                        break;
                case 2: hmChannelIDFeatures.put((String) key, (Double) value);
                        break;
                case 3: hmCategoryFeatures.put((String) key, (Double) value);
                        break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public HashMap<String, Double> getStringFeatures(int featureType) {
        switch (featureType) {
            case 1: return hmBoWFeatures;
            case 2: return hmChannelIDFeatures;
            case 3: return hmCategoryFeatures;
        }
        return null;
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
