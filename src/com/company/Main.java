package com.company;



import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException{

        /* Parse input for compressed option :
         "c"   -> Compressed
         default -> Uncompressed
         */

        boolean isCompressed = false;

        if(args.length != 0 && (args[0].equals("c") || args[0].equals("C"))) {
            isCompressed = true;
            System.out.println("Running in Mode : Compressed");
        }
        else
            System.out.println("Running in Mode : Uncompressed");


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

        /* All the maps, InvertedList are now written to disk */
        /* Test Retrieval Code */
//        try {
//            /* At object instantiate, constructor, reads all maps,
//            builds inverted list from file.
//           */
//            DiskReader diskReader = new DiskReader(isCompressed);
//            System.out.println(diskReader.getRetrievedlookUpTable().size());
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        /* Evaluation */
        Evaluation evaluation = new Evaluation();
        evaluation.runEvaluation();


    } /* End Of Main() */
}
