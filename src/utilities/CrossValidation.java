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

    public boolean loadData(List<Object> objectList) {

        arr = new Object[objectList.size()];

        int id = 0;
        for (Object obj:objectList)
            arr[id++] = obj;

        return true;
    }

    public List<Object> getTestingDataInFold(int numFold) {
        List<Object> listTest = new ArrayList<Object>();

        int testSize = arr.length / maxFold ;
        return null;
    }

    public List<Object>  getTrainingDataInFold(int numFold) {
        return null;

    }

}
