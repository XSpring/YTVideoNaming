package utilities;

import java.util.Set;

/**
 * Create on 3/11/14
 *
 * @author Loc Do
 */

public class CrossValidation {
    Object[] arr;

    public CrossValidation() {
        arr = null;
    }

    public boolean loadData(Set<Object> objectSet) {

        arr = new Object[objectSet.size()];

        int id = 0;
        for (Object obj:objectSet)
            arr[id++] = obj;

        return true;
    }

    public boolean getTrainingDataInFold(int numFold) {
        return true;

    }

}
