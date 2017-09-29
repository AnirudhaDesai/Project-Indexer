package com.company;
import org.json.simple.JSONObject;


import java.util.ArrayList;

public class Main {
    private static ArrayList<PlayData> playObjects;
    public static void main(String[] args) {
	    /* Extract the gz file to json. This will be commented for now
	     * since the extraction is already done */
        FileExtract fExtract = new FileExtract();
        fExtract.gunzipIt();

        //Read the json file and store data in playObjects
        playObjects = new ArrayList<PlayData>();
        playObjects = fExtract.readJSON();
        System.out.println(playObjects.size()
        );

    }
}
