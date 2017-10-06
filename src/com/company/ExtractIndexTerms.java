package com.company;


import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExtractIndexTerms {
    private static HashMap<String,ArrayList<Posting>> postings;
    private static HashMap<Integer, Long> docToSceneMap;
    private static HashMap<Integer, String> docToSceneIdMap;
    private static HashMap<String, Integer> sceneIdToDocMap;
    private static HashMap<String, String>  docToSceneIdPlayIdMap;
    private static HashMap<String, ArrayList<Integer>> playIdToDocMap;
    private static HashMap<Integer, Integer> docIdToLengthMap;
    private static HashMap<Integer, String[]> docToTermsMap;

    private float AverageLengthOfScenes;
    private PlayData ShortestScene;
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

    public PlayData getShortestScene() {
        return ShortestScene;
    }

    public void setShortestScene(PlayData shortestScene) {
        ShortestScene = shortestScene;
    }

    public float getAverageLengthOfScenes() {
        return AverageLengthOfScenes;
    }

    public void setAverageLengthOfScenes(float averageLengthOfScenes) {
        AverageLengthOfScenes = averageLengthOfScenes;
    }

    //    private static HashMap<Integer, ArrayList<String>> docToTermsMap;
    private static Integer documentId;

    public ExtractIndexTerms() {
        postings = new HashMap<>();
        docToSceneMap = new HashMap<>();
        docToSceneIdMap = new HashMap<>();
        sceneIdToDocMap = new HashMap<>();
        docToSceneIdPlayIdMap = new HashMap<>();
        playIdToDocMap = new HashMap<>();
        docIdToLengthMap = new HashMap<>();
        docToTermsMap = new HashMap<>();

    }

    protected void extractTerms(ArrayList<PlayData> obj){
        ArrayList<String>  docTerms = new ArrayList<>();
        ArrayList<Integer> docIdListForPlay = new ArrayList<>();
        int TotalLengthOfScenes = 0;   //To calculate Average Length of scenes
        int minSceneLength = Integer.MAX_VALUE;  // To calculate shortest scene
        documentId = 0;
        String holdPlayId = obj.get(0).getPlayId();
        for(int i = 0; i<obj.size();i++){
            if(!obj.get(i).getPlayId().equals(holdPlayId)){
                this.playIdToDocMap.put(holdPlayId, docIdListForPlay);
                docIdListForPlay = new ArrayList<>();
                holdPlayId = obj.get(i).getPlayId();
            }
            documentId++;    //Increment documentId for every scene
            docIdListForPlay.add(documentId);
            String sceneText = obj.get(i).getText();
            String[] words = sceneText.split("\\s+");
            docToTermsMap.put(documentId,words);
            TotalLengthOfScenes += words.length;   // For Average Scene Calculation

            docIdToLengthMap.put(documentId,words.length);

            /* Shortest Scene  */
            if(minSceneLength > words.length){
                this.setShortestScene(obj.get(i));
                minSceneLength = words.length;
            }

//            System.out.println(words.length);
            for(int j = 0; j<words.length; j++){
                if(!postings.containsKey(words[j])){
                    addNewPosting(words[j], obj.get(i).getSceneId(), obj.get(i).getSceneNum(), documentId, j);
                }
                else{
                    // term already exists. Update posting
                    updatePosting(words[j], obj.get(i).getSceneId(), obj.get(i).getSceneNum(), documentId, j);
                }
                docTerms.add(words[j]);
                /* Add this term to the Doc to Terms Map - Will be used for Retrieval */

            }

        }

        System.out.println("Add/update postings successful with size: "+this.postings.size());

        this.setAverageLengthOfScenes(TotalLengthOfScenes/(float)obj.size());
        System.out.println("The average length of a Scene is :" + this.getAverageLengthOfScenes());

        System.out.println("The shortest scene is : "+this.getShortestScene().getSceneId()+
                    " from play : "+ this.getShortestScene().getPlayId()+" with scene length : " +
                                this.getShortestScene().getText().split("\\s+").length);


        getLongestShortestPlays();

        /* Write the Maps to Disk */
        writeMapsToDisk();

    }
    public void addNewPosting(String word, String sceneId, Long sceneNum, Integer DocId, Integer pos){
        ArrayList<Posting> temp = new ArrayList<>();
        Posting postObject = new Posting();
        ArrayList<Integer> posList = new ArrayList<>();
        postObject.setDocId(DocId);
        postObject.setTermFreq(1);
        posList.add(pos);
        temp.add(postObject);
        postObject.setPos(posList);
        this.postings.put(word, temp);

        /* Add a map from DocId to SceneId */
        docToSceneIdMap.put(DocId, sceneId);

        /*Add a map from DocId to Scene Number */
        docToSceneMap.put(DocId,sceneNum);

        /*Add a map from SceneId to DocId */
        sceneIdToDocMap.put(sceneId, DocId);

    }

    public void updatePosting(String word, String sceneId, Long sceneNum, Integer DocId, Integer pos){
        ArrayList<Posting> wordData = this.postings.get(word);

        boolean docIdFound = false;
        Posting updateRecord = new Posting();
        int docIndex = -1;
        /* Loop through wordData to find the DocId list */
        for(int i = 0; i<wordData.size();i++){
            if(wordData.get(i).getDocId()==DocId) {
                docIdFound = true;
                updateRecord = wordData.get(i);
                docIndex = i;
                break;
            }
        }
        if(docIdFound){
            /*Posting for this Doc Id already exists for this word. Update the position list */
            updateRecord.getPos().add(pos);
            updateRecord.setTermFreq(updateRecord.getTermFreq()+1);

            /* Update postings with the new list */
            wordData.set(docIndex, updateRecord);
            this.postings.put(word, wordData);

        }
        else{
            /*Record for Document Id not present. Add new one. */
            Posting addRecord = new Posting(DocId, 1);
            addRecord.setPos(new ArrayList<>(Arrays.asList(pos)));
            wordData.add(addRecord);
            /* Finally, add this to the postings */
            this.postings.put(word, wordData);

        }

    }

    private void writeMapsToDisk(){
        ObjectMapper mapper = new ObjectMapper();
        String path = "..//";

        try {
            mapper.writeValue(new File(path+"docToSceneIdMap.json"), docToSceneIdMap);
            mapper.writeValue(new File(path+"docToSceneMap.json"), docToSceneMap);
            mapper.writeValue(new File(path+"sceneIdToDocMap.json"), sceneIdToDocMap);
            mapper.writeValue(new File(path+"playIdToDocMap.json"), playIdToDocMap);
            mapper.writeValue(new File(path+"docToTermsMap.json"), docToTermsMap);
            mapper.writeValue(new File(path+"docIdToLengthMap.json"), docIdToLengthMap);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void getLongestShortestPlays(){
        int maxPlayLength = Integer.MIN_VALUE, minPlayLength = Integer.MAX_VALUE;
        int playLength =  0;
        for(HashMap.Entry<String,ArrayList<Integer>> PlayToDoc : this.playIdToDocMap.entrySet()){
            playLength = 0;
            String playId = PlayToDoc.getKey();
            ArrayList<Integer> docList = PlayToDoc.getValue();
            for(Integer docId : docList)
                playLength += this.docIdToLengthMap.get(docId);
            if(minPlayLength>playLength){
                this.setShortestPlay(playId);
                minPlayLength = playLength;

            }
            if(maxPlayLength<playLength){
                this.setLongestPlay(playId);
                maxPlayLength = playLength;
            }
        }

        System.out.println("The longest play is : "+this.getLongestPlay());
        System.out.println("The shortest play is : "+this.getShortestPlay());

    }


    protected static HashMap<String, ArrayList<Posting>> getPostings() {
        return postings;
    }

    public static void setPostings(HashMap<String, ArrayList<Posting>> postings) {
        ExtractIndexTerms.postings = postings;
    }

    public static HashMap<Integer, Long> getDocToSceneMap() {
        return docToSceneMap;
    }

    public static HashMap<String, Integer> getSceneIdToDocMap() {
        return sceneIdToDocMap;
    }

    public static void setSceneIdToDocMap(HashMap<String, Integer> sceneIdToDocMap) {
        ExtractIndexTerms.sceneIdToDocMap = sceneIdToDocMap;
    }

    public static HashMap<String, String> getDocToSceneIdPlayIdMap() {
        return docToSceneIdPlayIdMap;
    }

    public static void setDocToSceneIdPlayIdMap(HashMap<String, String> docToSceneIdPlayIdMap) {
        ExtractIndexTerms.docToSceneIdPlayIdMap = docToSceneIdPlayIdMap;
    }


    public static Integer getDocumentId() {
        return documentId;
    }

    public static void setDocumentId(Integer documentId) {
        ExtractIndexTerms.documentId = documentId;
    }

    public static void setDocToSceneMap(HashMap<Integer, Long> docToSceneMap) {
        ExtractIndexTerms.docToSceneMap = docToSceneMap;
    }

    public static HashMap<Integer, String> getDocToSceneIdMap() {
        return docToSceneIdMap;
    }

    public static void setDocToSceneIdMap(HashMap<Integer, String> docToSceneIdMap) {
        ExtractIndexTerms.docToSceneIdMap = docToSceneIdMap;
    }


}
