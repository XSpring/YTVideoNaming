/**
 * Create on Nov, 2, 2014
 */

package controllers;

import models.model;
import models.regressionModel;

/**
 * @author Loc Do
 */

public class mainController {

    public static void main(String[] args) {
        System.out.println("This is the main controllers...");

        model newModel = new regressionModel();
        newModel.run();
    }

}
