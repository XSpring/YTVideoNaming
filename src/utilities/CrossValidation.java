package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create on 3/11/14
 *
 * @author Loc Do
 */

public class CrossValidation {
    Object[] arr;

    int maxFold = 5;

    int testSize = 0;

    public CrossValidation() {
        arr = null;
    }

    public CrossValidation(int newMaxFold) {

        arr = null;

        maxFold = newMaxFold;
    }

    public boolean loadData(Set<Object> objectSet) {

        arr = new Object[objectSet.size()];

        int id = 0;
        for (Object obj:objectSet)
            arr[id++] = obj;

        return true;
    }

    public boolean loadData(List<String> objectList) {

        arr = new Object[objectList.size()];

        int id = 0;
        for (Object obj:objectList)
            arr[id++] = obj;

        testSize = arr.length / maxFold ;

        return true;
    }

    public List<Object> getTestingDataInFold(int numFold) {
        List<Object> lstTest = new ArrayList<Object>();

        int start = (numFold - 1)*testSize;
        int end = numFold*testSize;
        if (numFold==maxFold)
            end = arr.length;

        for (int id = start; id < end; id++)
            lstTest.add(arr[id]);

        return lstTest;
    }

    public List<Object>  getTrainingDataInFold(int numFold) {
        List<Object> lstTrain = new ArrayList<Object>();

        int start = (numFold - 1)*testSize;
        int end = numFold*testSize;
        if (numFold==maxFold)
            end = arr.length;

        for (int id = 0; id < start; id++)
            lstTrain.add(arr[id]);

        for (int id = end; id < arr.length; id++)
            lstTrain.add(arr[id]);
        return lstTrain;

    }

}
