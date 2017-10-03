package com.company;
import org.json.simple.JSONObject;


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	    /* Extract the gz file to json. This will be commented for now
	     * since the extraction is already done */
        FileExtract fExtract = new FileExtract();
//        fExtract.gunzipIt();

        /* Read the json file and store data in playObjects */

        ArrayList<PlayData> allScenes = new ArrayList<PlayData>();
        allScenes = fExtract.readJSON();

        /*Extract Index Terms from the scene data */

        ExtractIndexTerms indexPostings = new ExtractIndexTerms();
        indexPostings.extractTerms(allScenes);
        // Test the postings
        System.out.println("(Main)Add/update postings successful with size: "+ indexPostings.getPostings().size());

        /* Create Inverted Index converting the ArrayList<Posting> into a single <String, ArrayList<Integer>>
        to write to file. This can be debated to as a redundant step. It is added for better clarity of results.
         */

        InvertedIndex invIndices = new InvertedIndex();
        invIndices.createInvIndex(indexPostings.getPostings());

        System.out.println("(Main) Creation of Inverted Index Successful with size : " + invIndices.getInvIndex().size());
        System.out.println(invIndices.getInvIndex().get("the"));




    }
}
