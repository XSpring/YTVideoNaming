/**
 * Create on Nov, 2, 2014
 */

package controllers;

import controllers.dataControllers.dataController;
import controllers.dataControllers.videoController;

/**
 * @author Loc Do
 */

public class mainController {

    public static void main(String[] args) {
        System.out.println("This is the main controllers...");

        String videoFolder = "YTData";
        //String videoFolder = "youtube";
        try {
            dataController.run(videoFolder);
            videoController.run();
        } catch (java.io.IOException e) {
            System.err.println("Could not run UserDictionaryMaker: ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //model newModel = new regressionModel();
        //newModel.run();
    }

}
