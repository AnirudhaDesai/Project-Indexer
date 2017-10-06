package com.company;

import java.io.IOException;
import java.util.*;

public class RetrievalAPI {
    DiskReader diskReader;
    ArrayList<PlayData> retrievalResults;
    private ArrayList<String> vocabulary;
    private boolean isCompressed;

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(ArrayList<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public RetrievalAPI() {
        diskReader = new DiskReader();
        vocabulary = new ArrayList<>();
        retrievalResults = new ArrayList<>();
        this.setCompressed(false);
        buildVocabulary();
    }
    /* Overloaded Constructor for compressed case*/
    public RetrievalAPI(boolean isCompressed) throws IOException{
        diskReader = new DiskReader(isCompressed);
        vocabulary = new ArrayList<>();
        retrievalResults = new ArrayList<>();
        this.setCompressed(isCompressed);
        buildVocabulary();
    }
    private void buildVocabulary(){
        for(HashMap.Entry<String, PostingListDisk> pair : diskReader.getRetrievedlookUpTable().entrySet()){
            this.vocabulary.add(pair.getKey());
        }
        System.out.println("The vocabulary of size : "+this.vocabulary.size()+ " is : ");

    }

    public void getFrequenciesForTerm(String query, ArrayList<TermDocFrequency> result) throws IOException{
        String[] queryWords = query.split("\\s+");

        for(String word: queryWords) {
            TermDocFrequency freqObject = new TermDocFrequency();
            freqObject.setTerm(word);
            freqObject.setTermFrequency(diskReader.getPostingListDiskObjectForTerm(word).getTermFrequency());
            freqObject.setDocFrequency(diskReader.getPostingListDiskObjectForTerm(word).getDocFrequency());
            result.add(freqObject);
//            System.out.printf("The term frequency, DocFrequency for %s is : %d,%d \n",word,
//                    freqObject.getTermFrequency(),freqObject.getDocFrequency());
        }

    }

    public ArrayList<PlayData> RetrieveQuery(String query, int k) throws IOException{
        HashMap<Integer, Integer> docIdToScores = new HashMap<>();
        retrievalResults  = new ArrayList<>();
        ArrayList<ArrayList<Integer>> queryInvLists = new ArrayList<>();
        query = query.trim();
        String[] queryWords = query.split("\\s+");
        for(String word : queryWords){
               queryInvLists.add(diskReader.getInvertedListForTerm(word));
           }
        for(ArrayList<Integer> invList : queryInvLists){
               int i = 0;
               while(i<invList.size()){
                   docIdToScores.put(invList.get(i),docIdToScores.getOrDefault(invList.get(i),
                            0)+invList.get(i+1));   // Add the term frequency in the document to score
                   i = i+invList.get(i+1)+2;
               }
           }

        /* All doc Id Scores for this query in HashMap. Now use priority queue to get top k */

        List<Integer> topKDocs = getTopKDocs(docIdToScores, k);
        for(Integer docId : topKDocs){
            PlayData pObj = new PlayData();
            pObj.setSceneId(diskReader.getRetrievedDocToSceneIdMap().get(docId));
            pObj.setSceneNum(diskReader.getRetrievedDocToSceneMap().get(docId));

            retrievalResults.add(pObj);
        }
        return  retrievalResults;

    }

    private List<Integer> getTopKDocs(HashMap<Integer,Integer> docIdToScores, int  k){
        PriorityQueue<Integer> topK = new PriorityQueue<Integer>(k, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return Double.compare(docIdToScores.get(i1), docIdToScores.get(i2));
            }
        });

        for(Integer key:docIdToScores.keySet()){
            if (topK.size() < k)
                topK.add(key);
            else if (docIdToScores.get(topK.peek()) < docIdToScores.get(key)) {
                topK.poll();
                topK.add(key);
            }
        }
        return (List)Arrays.asList(topK.toArray());
    }

}
