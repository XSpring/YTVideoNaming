/**
 * Create on Nov, 2, 2014
 */

package controllers;

import controllers.dataControllers.dataController;
import controllers.dataControllers.videoController;
import utilities.Configuration;

/**
 * @author Loc Do
 */

public class mainController {

    public static void main(String[] args) {
        System.out.println("This is the main controllers...");

        String videoFolder = "data_all";
        //String videoFolder = "youtube";
        try {
            System.out.println(args.length);
                switch (args.length) {
                    case 1:
                        Configuration.getInstance().setFileOutputName(args[0]); break;
                    case 2:
                        Configuration.getInstance().setFileOutputName(args[0]);
                        if (Integer.parseInt(args[1])>0)
                            Configuration.getInstance().setAug(true);
                        break;
                    case 3:
                        Configuration.getInstance().setFileOutputName(args[0]);
                        if (Integer.parseInt(args[1])>0)
                            Configuration.getInstance().setAug(true);
                        if (Integer.parseInt(args[2])>0)
                            Configuration.getInstance().setGrad(true);
                        break;
                    case 4:
                        Configuration.getInstance().setFileOutputName(args[0]);
                        if (Integer.parseInt(args[1])>0)
                            Configuration.getInstance().setAug(true);
                        if (Integer.parseInt(args[2])>0)
                            Configuration.getInstance().setGrad(true);
                        if (Integer.parseInt(args[3])>0)
                            Configuration.getInstance().setBagging(true);
                        break;
                    default: System.out.println("Running with default settings...");
                        break;
                }
            System.out.println(Configuration.getInstance().getFileOutputName()+" "+
                                Configuration.getInstance().isAug()+" "+
                                Configuration.getInstance().isBagging()+" "+
                                Configuration.getInstance().isGrad());

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
