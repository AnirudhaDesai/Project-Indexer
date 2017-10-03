package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InvertedIndex {
    private static HashMap<String, ArrayList<Long>> invIndex;

    public void createInvIndex(HashMap<String,ArrayList<Posting>> postings ){
        /* This function takes postings HashMap as input and combines the
        data from all the document objects for a term into one List.
         */

        for(HashMap.Entry<String,ArrayList<Posting>> term: postings.entrySet()){

                String word = term.getKey();
                ArrayList<Posting> docObj = term.getValue();
                ArrayList<Long> invIndexPosting = new ArrayList<>();
                /* For each term, iterate through the Posting Objects */
                for(Posting doc : docObj){
                        invIndexPosting.add(doc.getDocId());
                        invIndexPosting.add(doc.getTermFreq());
                        invIndexPosting.addAll(doc.getPos());
                }

                /* Add the List of Documents, Term Frequency and Positions data to the map */
                invIndex.put(word,invIndexPosting);
        }
        return;
    }

    public InvertedIndex() {
        invIndex = new HashMap<>();
    }


    public static HashMap<String, ArrayList<Long>> getInvIndex() {
        return invIndex;
    }

    public static void setInvIndex(HashMap<String, ArrayList<Long>> invIndex) {
        InvertedIndex.invIndex = invIndex;
    }
}
