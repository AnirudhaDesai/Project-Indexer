package com.company;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExtractIndexTerms {
    private static HashMap<String,ArrayList<Posting>> postings;
    private static HashMap<Long, Long> docToSceneMap;
    private static HashMap<Long, String> docToSceneIdMap;
    private static Long documentId;

    public ExtractIndexTerms() {
        this.postings = new HashMap<>();
        this.docToSceneMap = new HashMap<Long, Long>();
        this.docToSceneIdMap = new HashMap<Long, String>();

    }

    public void extractTerms(ArrayList<PlayData> obj){
        documentId = new Long(0);
        for(int i = 0; i<obj.size();i++){
            documentId++;    //Increment documentId for every scene
            String sceneText = obj.get(i).getText();
            String[] words = sceneText.split("\\s+");
//            System.out.println(words.length);
            for(Long j = new Long(0); j<words.length; j++){
                if(!this.postings.containsKey(words[j.intValue()])){
                    addNewPosting(words[j.intValue()], obj.get(i).getSceneId(), obj.get(i).getSceneNum(), documentId, j);
                }
                else{
                    // term already exists. Update posting
                    updatePosting(words[j.intValue()], obj.get(i).getSceneId(), obj.get(i).getSceneNum(), documentId, j);
                }
            }
        }

        System.out.println("Add/update postings successful with size: "+this.postings.size());


    }
    public void addNewPosting(String word, String sceneId, Long sceneNum, Long DocId, Long pos){
        ArrayList<Posting> temp = new ArrayList<>();
        Posting postObject = new Posting();
        ArrayList<Long> posList = new ArrayList<>();
        postObject.setDocId(DocId);
        postObject.setTermFreq(new Long(1));
        posList.add(pos);
        temp.add(postObject);
        postObject.setPos(posList);
        this.postings.put(word, temp);

        /* Add a map from DocId to SceneId */
        docToSceneIdMap.put(DocId, sceneId);

        /*Add a map from DocId to Scene Number */
        docToSceneMap.put(DocId,sceneNum);

    }

    public void updatePosting(String word, String sceneId, Long sceneNum, Long DocId, Long pos){
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
            Posting addRecord = new Posting(DocId, new Long(1 ));
            addRecord.setPos(new ArrayList<>(Arrays.asList(pos)));
            wordData.add(addRecord);
            /* Finally, add this to the postings */
            this.postings.put(word, wordData);

        }

    }


    public static HashMap<String, ArrayList<Posting>> getPostings() {
        return postings;
    }

    public static void setPostings(HashMap<String, ArrayList<Posting>> postings) {
        ExtractIndexTerms.postings = postings;
    }

    public static HashMap<Long, Long> getDocToSceneMap() {
        return docToSceneMap;
    }

    public static void setDocToSceneMap(HashMap<Long, Long> docToSceneMap) {
        ExtractIndexTerms.docToSceneMap = docToSceneMap;
    }

    public static HashMap<Long, String> getDocToSceneIdMap() {
        return docToSceneIdMap;
    }

    public static void setDocToSceneIdMap(HashMap<Long, String> docToSceneIdMap) {
        ExtractIndexTerms.docToSceneIdMap = docToSceneIdMap;
    }


}
