package controllers.dataControllers;

import java.util.HashMap;

/**
 * Create on 30/11/14
 *
 * @author Loc Do
 */
public class FeatureController {
    public static final int NUMERICFEATURES = 0;
    public static final int BAGOFWORDSFEATURES = 1;
    public static final int CHANNELIDFEATURES = 2;
    public static final int CATEGORYFEATURES = 3;
    
    HashMap<Integer, Double> hmNumericFeatures = null; // type = 0
    HashMap<String, Double> hmBoWFeatures = null; // type = 1
    HashMap<String, Double> hmChannelIDFeatures = null; // type = 2
    HashMap<String, Double> hmCategoryFeatures = null; // type = 3

    public FeatureController() {
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
    
    public void output(String filename) {
	java.io.FileWriter fw = null;
	java.io.BufferedWriter bw = null;
	try {
	    fw = new java.io.FileWriter(filename);
	    bw = new java.io.BufferedWriter(fw);
	    for (Integer j : hmNumericFeatures.keySet()) {
		bw.write("0." + j + ";" + hmNumericFeatures.get(j));
		bw.newLine();
	    }
	    for (int i=1; i<4; i++) {
		for (String j : getStringFeatures(i).keySet()) {
		    bw.write(i + "." + j + ";" + getOrInitFeature(i, j));
		    bw.newLine();
		}
	    }
	} catch (java.io.IOException e) {
	    System.err.println("Failed to finish output file " + filename);
	} finally {
	    try {
		bw.close();
		fw.close();
	    } catch (java.io.IOException e) {};
	}
    }
}
