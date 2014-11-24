/**
 * Create on Nov, 2, 2014
 */

package controllers;

import controllers.dataControllers.videoController;

/**
 * @author Loc Do
 */

public class mainController {

    public static void main(String[] args) {
        System.out.println("This is the main controllers...");

        String videoFolder = "/data/YouTube";
        //String videoFolder = "youtube";
        try {
            videoController.run(videoFolder);
        } catch (java.io.IOException e) {
            System.err.println("Could not run UserDictionaryMaker: ");
            e.printStackTrace();
        }

        //model newModel = new regressionModel();
        //newModel.run();
    }

}
