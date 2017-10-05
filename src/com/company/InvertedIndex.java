package com.company;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class InvertedIndex {
    private static HashMap<String, ArrayList<Integer>> invIndex;

    public void createInvIndex(HashMap<String,ArrayList<Posting>> postings ){
        /* This function takes postings HashMap as input and combines the
        data from all the document objects for a term into one List.
         */

        for(HashMap.Entry<String,ArrayList<Posting>> term: postings.entrySet()){

                String word = term.getKey();
                ArrayList<Posting> docObj = term.getValue();
                ArrayList<Integer> invIndexPosting = new ArrayList<>();
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

    public void createInvIndexCompressed(HashMap<String,ArrayList<Posting>> postings ){
        /* This function takes postings HashMap as input and combines the
        data from all the document objects for a term into one List with delta Encoding
        and V-byte Encoding.
         */

        for(HashMap.Entry<String,ArrayList<Posting>> term: postings.entrySet()){

            String word = term.getKey();
            ArrayList<Posting> docObj = term.getValue();
            ArrayList<Integer> invIndexPosting = new ArrayList<>();
            int docIdHolder = 0;
                /* For each term, iterate through the Posting Objects */
            for(Posting doc : docObj){
                invIndexPosting.add(doc.getDocId()-docIdHolder);
                docIdHolder = doc.getDocId();
                invIndexPosting.add(doc.getTermFreq());
                ArrayList<Integer> posList = doc.getPos();
                deltaEncode(posList);
                invIndexPosting.addAll(posList);

            }

                /* Add the List of Documents, Term Frequency and Positions data to the map */
            invIndex.put(word,invIndexPosting);
        }
        return;
    }

    private void deltaEncode(ArrayList<Integer> arr){
        if(arr.size()==0) return;
        int holder = arr.get(0);
        for(int i = 1;i<arr.size();i++){
            arr.set(i, arr.get(i)-holder);
            holder = holder + arr.get(i);
        }
    }


    public InvertedIndex() {
        invIndex = new HashMap<>();
    }


    public static HashMap<String, ArrayList<Integer>> getInvIndex() {
        return invIndex;
    }

    public static void setInvIndex(HashMap<String, ArrayList<Integer>> invIndex) {
        InvertedIndex.invIndex = invIndex;
    }
}
