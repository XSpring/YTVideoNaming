package com.cmu;

import dataController.youtubeCrawler;

public class Main {

    public static void main(String[] args) {
	    // write your code here
        System.out.println("Running the crawler with seed " + args[0]);
        youtubeCrawler worker = new youtubeCrawler();
        //String seedURL = "Hr1fFMp0MqU";
        String seedURL = args[0];
        worker.run(seedURL);
    }
}
