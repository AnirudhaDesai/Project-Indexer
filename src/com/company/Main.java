package com.company;



import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        /* Parse input for compressed option :
         "c"   -> Compressed
         default -> Uncompressed
         */

        boolean isCompressed = false;

        if(args.length != 0 && (args[0].equals("c") || args[0].equals("C")))
            isCompressed = true;


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

        System.out.println("(Main)Add/update postings successful with size: "+ indexPostings.getPostings().size());

        /* Create Inverted Index converting the ArrayList<Posting> into a single <String, ArrayList<Integer>>
        to write to file. This can be debated to as a redundant step. It is added for better clarity of results.
         */

        InvertedIndex invIndices = new InvertedIndex();
        if(!isCompressed)
            invIndices.createInvIndex(indexPostings.getPostings());
        else
            invIndices.createInvIndexCompressed(indexPostings.getPostings());

        System.out.println("(Main) Creation of Inverted Index Successful with size : " + invIndices.getInvIndex().size());

        /* Write Inverted Index and other ancillary data structures to disk */
        DiskWriter diskWriter = new DiskWriter(isCompressed);
        try {
            if(!isCompressed)
                diskWriter.writeInvIndexToFile(invIndices);
            else
                diskWriter.writeCompressedInvIndexToFile(invIndices);
        }catch (IOException e){
            e.printStackTrace();
        }

    } /* End Of Main() */
}
