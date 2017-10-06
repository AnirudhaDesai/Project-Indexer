package com.company;

import java.io.IOException;
import java.util.*;

public class RetrievalAPI {
    DiskReader diskReader;
    ArrayList<PlayData> retrievalResults;
    private ArrayList<String> vocabulary;
    private boolean isCompressed;

    private float AverageLengthOfScenes;
    private String ShortestScene;
    private String LongestPlay;
    private String ShortestPlay;

    public String getLongestPlay() {
        return LongestPlay;
    }

    public void setLongestPlay(String longestPlay) {
        LongestPlay = longestPlay;
    }

    public String getShortestPlay() {
        return ShortestPlay;
    }

    public void setShortestPlay(String shortestPlay) {
        ShortestPlay = shortestPlay;
    }

    public String getShortestScene() {
        return ShortestScene;
    }

    public void setShortestScene(String shortestScene) {
        ShortestScene = shortestScene;
    }

    public RetrievalAPI() {
        diskReader = new DiskReader();
        vocabulary = new ArrayList<>();
        retrievalResults = new ArrayList<>();
        this.setCompressed(false);
        buildVocabulary();
        calcAllStats();
    }
    /* Overloaded Constructor for compressed case*/
    public RetrievalAPI(boolean isCompressed) throws IOException{
        diskReader = new DiskReader(isCompressed);
        vocabulary = new ArrayList<>();
        retrievalResults = new ArrayList<>();
        this.setCompressed(isCompressed);
        buildVocabulary();
        calcAllStats();
    }
    private void buildVocabulary(){
        for(HashMap.Entry<String, PostingListDisk> pair : diskReader.getRetrievedlookUpTable().entrySet()){
            this.vocabulary.add(pair.getKey());
        }
        System.out.println("Vocabulary size : "+this.vocabulary.size());

    }

    public void getFrequenciesForTerm(String query, ArrayList<TermDocFrequency> result) throws IOException{
        String[] queryWords = query.split("\\s+");

        for(String word: queryWords) {
            TermDocFrequency freqObject = new TermDocFrequency();
            freqObject.setTerm(word);
            freqObject.setTermFrequency(diskReader.getPostingListDiskObjectForTerm(word).getTermFrequency());
            freqObject.setDocFrequency(diskReader.getPostingListDiskObjectForTerm(word).getDocFrequency());
            result.add(freqObject);

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

    public String getHighestScoringPhrase(String query) throws IOException{
        if(query.length()==0) return null;
        String[] terms = query.split("\\s+");
        StringBuilder fullQuery = new StringBuilder();
        for (String term : terms){
            fullQuery.append(term+" "+diskReader.getHighestScoringTerm(term)+" ");
        }
        return fullQuery.toString().trim();
    }

    private void calcAllStats(){
        calculateSceneStats();
        calculatePlayStats();

    }
    private void calculatePlayStats(){
        String holdPlayId = getPlayFromMetaData(diskReader.getRetrievedDocToSceneIdPlayIdMap().get(1));
        int playLength = 0;
        int minPlayLength = Integer.MAX_VALUE;
        int maxPlayLength = Integer.MIN_VALUE;
        for(HashMap.Entry<Integer, String> pair : diskReader.getRetrievedDocToSceneIdPlayIdMap().entrySet()) {
            String playId = getPlayFromMetaData(pair.getValue());
            int docNum = pair.getKey();

            if(!playId.equals(holdPlayId)){
                if(minPlayLength > playLength){
                    minPlayLength = playLength;
                    setShortestPlay(holdPlayId);
                }
                if(maxPlayLength<playLength){
                    maxPlayLength = playLength;
                    setLongestPlay(holdPlayId);
                }
                playLength = 0;
                holdPlayId = playId;
            }
            playLength += diskReader.getRetrievedDocToLengthMap().get(docNum);
        }

        System.out.println("Longest Play : "+getLongestPlay());
        System.out.println("Shortest Play : "+getShortestPlay());
    }
    private String getPlayFromMetaData(String metaData){
        String[] hold = metaData.split("\\$");
        return hold[1];
    }

    private void calculateSceneStats(){
        int totalLength = 0;
        int numScenes = 0;
        float averageSceneLength = 0;
        int minSceneLength = Integer.MAX_VALUE;
        for(HashMap.Entry<Integer, Integer> pair : diskReader.getRetrievedDocToLengthMap().entrySet()){
            totalLength += pair.getValue();
            numScenes += 1;
            if(minSceneLength>pair.getValue()){
                this.setShortestScene(diskReader.getRetrievedDocToSceneIdMap().get(pair.getKey()));
                minSceneLength = pair.getValue();
            }
        }
        averageSceneLength = (float) totalLength / (float) numScenes;
        this.setAverageLengthOfScenes(averageSceneLength);
        System.out.println("Average Scene Length : "+averageSceneLength);
        System.out.println("Shortest Scene : "+this.getShortestScene());

    }

    public float getAverageLengthOfScenes() {
        return AverageLengthOfScenes;
    }

    public void setAverageLengthOfScenes(float averageLengthOfScenes) {
        AverageLengthOfScenes = averageLengthOfScenes;
    }

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



}
