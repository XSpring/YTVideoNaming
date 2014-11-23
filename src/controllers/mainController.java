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

        String videoFolder = "youtube";
        String outputFileName = "youtube/videoData.txt";
        try {
            videoController.run(videoFolder, outputFileName);
        } catch (java.io.IOException e) {
            System.err.println("Could not run UserDictionaryMaker: ");
            e.printStackTrace();
        }

        //model newModel = new regressionModel();
        //newModel.run();
    }

}
