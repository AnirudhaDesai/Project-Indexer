package com.company;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class DiskReader {
    private String path;
    private static HashMap<String, PostingListDisk> RetrievedLookUpTable;
    private static HashMap<String, ArrayList<Integer>> RetrievedInvList;
    private static HashMap<Integer, Long> RetrievedDocToSceneMap;
    private static HashMap<Integer, String> RetrievedDocToSceneIdMap;
    private static HashMap<Integer, String> RetrievedDocToSceneIdPlayIdMap;
    private static HashMap<String, Integer> RetrievedSceneIdToDocMap;
    private static HashMap<Integer, String[]> RetrievedDocToTermsMap;
    private static HashMap<Integer, Integer> RetrievedDocToLengthMap;

    public static HashMap<Integer, String[]> getRetrievedDocToTermsMap() {
        return RetrievedDocToTermsMap;
    }

    public static void setRetrievedDocToTermsMap(HashMap<Integer, String[]> retrievedDocToTermsMap) {
        RetrievedDocToTermsMap = retrievedDocToTermsMap;
    }

    public static HashMap<Integer, Integer> getRetrievedDocToLengthMap() {
        return RetrievedDocToLengthMap;
    }

    public static void setRetrievedDocToLengthMap(HashMap<Integer, Integer> retrievedDocToLengthMap) {
        RetrievedDocToLengthMap = retrievedDocToLengthMap;
    }

    RandomAccessFile reader;
    private boolean isCompressed;

    public DiskReader() {
        setPath("..//");
        RetrievedLookUpTable = new HashMap<>();
        RetrievedInvList = new HashMap<>();
        RetrievedDocToSceneIdMap = new HashMap<>();
        RetrievedDocToSceneIdPlayIdMap = new HashMap<>();
        RetrievedDocToSceneMap = new HashMap<>();
        RetrievedSceneIdToDocMap = new HashMap<>();
        RetrievedDocToTermsMap = new HashMap<>();
        try {
            reader = new RandomAccessFile(path + "InvertedList.dat", "r");
        }catch(IOException e){
            e.printStackTrace();
        }

//        readLookUpTable();
        try{
            readAllMapsFromDisk();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    /* Overloaded Constructor to read if using Compressed Index */
    public DiskReader (boolean isCompressed) throws IOException {
        setPath("..//");
        this.setCompressed(isCompressed);
        RetrievedLookUpTable = new HashMap<>();
        RetrievedInvList = new HashMap<>();
        RetrievedDocToSceneIdMap = new HashMap<>();
        RetrievedDocToSceneIdPlayIdMap = new HashMap<>();
        RetrievedDocToSceneMap = new HashMap<>();
        RetrievedSceneIdToDocMap = new HashMap<>();
        RetrievedDocToTermsMap = new HashMap<>();
        if(!isCompressed) {
            reader = new RandomAccessFile(path + "InvertedList.dat", "r");
            /* Build Inverted List from file - This is for Sanity Check only.*/
//            readInvListFromDisk();
        }
        else {
            reader = new RandomAccessFile(path + "InvertedListCompressed.dat", "r");
            /* Build Inverted List from File  - Sanity Check only. Not part of original code*/

//            readCompressedInvListFromDisk();
        }


        try{
            readAllMapsFromDisk();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readInvListFromDisk() throws IOException{
        readLookUpTable();   /* Retrieves the lookUpTable with Term->Offset mappings from disk */

        for(HashMap.Entry<String, PostingListDisk> pair : this.RetrievedLookUpTable.entrySet()){
            ArrayList<Integer> termInvertedList = new ArrayList<>();
            String term = pair.getKey();
            PostingListDisk pObject = pair.getValue();

            byte[] byteList = new byte[pObject.getLen()];
            reader.seek(pObject.getOffset());
            reader.read(byteList,0,pObject.getLen());
            IntBuffer buffer = ByteBuffer.wrap(byteList).asIntBuffer();
            /* Write inverted List from buffer*/
            while(buffer.hasRemaining()){
                termInvertedList.add(buffer.get());
            }

            /* Add record to invList map */
            this.RetrievedInvList.put(term, termInvertedList);

        }

    }



    public void readCompressedInvListFromDisk() throws IOException{
        readLookUpTable(true);   /* Retrieves the lookUpTable with Term->Offset mappings from disk */

        for(HashMap.Entry<String, PostingListDisk> pair : this.RetrievedLookUpTable.entrySet()){
            ArrayList<Integer> termInvertedList = new ArrayList<>();
            String term = pair.getKey();
            PostingListDisk pObject = new PostingListDisk();
            pObject = (PostingListDisk) pair.getValue();

            byte[] byteList = new byte[pObject.getLen()];
            reader.seek(pObject.getOffset());
            reader.read(byteList,0,pObject.getLen());
            ByteBuffer buffer = ByteBuffer.wrap(byteList);
            /* Decode v-byte encoded byte buffer */
            decode(buffer.array(), termInvertedList);
            /* Add record to invList map */
            this.RetrievedInvList.put(term, termInvertedList);

        }

            System.out.println("Compressed Inverted list read successfully");

    }

    public static HashMap<String, PostingListDisk> getRetrievedLookUpTable() {
        return RetrievedLookUpTable;
    }

    public static void setRetrievedLookUpTable(HashMap<String, PostingListDisk> retrievedLookUpTable) {
        RetrievedLookUpTable = retrievedLookUpTable;
    }

    public static HashMap<Integer, Long> getRetrievedDocToSceneMap() {
        return RetrievedDocToSceneMap;
    }

    public static void setRetrievedDocToSceneMap(HashMap<Integer, Long> retrievedDocToSceneMap) {
        RetrievedDocToSceneMap = retrievedDocToSceneMap;
    }

    public static HashMap<Integer, String> getRetrievedDocToSceneIdMap() {
        return RetrievedDocToSceneIdMap;
    }

    public static void setRetrievedDocToSceneIdMap(HashMap<Integer, String> retrievedDocToSceneIdMap) {
        RetrievedDocToSceneIdMap = retrievedDocToSceneIdMap;
    }

    public static HashMap<String, Integer> getRetrievedSceneIdToDocMap() {
        return RetrievedSceneIdToDocMap;
    }

    public static void setRetrievedSceneIdToDocMap(HashMap<String, Integer> retrievedSceneIdToDocMap) {
        RetrievedSceneIdToDocMap = retrievedSceneIdToDocMap;
    }

    public void readAllMapsFromDisk() throws IOException{
        readLookUpTable(this.isCompressed());
        ObjectMapper mapper = new ObjectMapper();

            RetrievedDocToSceneIdMap = (mapper.readValue(new File(path + "docToSceneIdMap.json"),
                    new TypeReference<HashMap<Integer, String>>() {
                    }));
            RetrievedDocToSceneMap = (mapper.readValue(new File(path + "docToSceneMap.json"),
                    new TypeReference<HashMap<Integer, Long>>() {
                    }));
            RetrievedSceneIdToDocMap = (mapper.readValue(new File(path + "sceneIdToDocMap.json"),
                    new TypeReference<HashMap<String, Integer>>() {
                    }));
            RetrievedDocToTermsMap = (mapper.readValue(new File(path + "docToTermsMap.json"),
                    new TypeReference<HashMap<Integer, String[]>>() {
                    }));
            RetrievedDocToLengthMap = (mapper.readValue(new File(path + "docIdToLengthMap.json"),
                new TypeReference<HashMap<Integer, Integer>>() {
                }));
            RetrievedDocToSceneIdPlayIdMap = (mapper.readValue(new File(path + "docToSceneIdPlayIdMap.json"),
                new TypeReference<HashMap<Integer, String>>() {
                }));
    }


    public static HashMap<Integer, String> getRetrievedDocToSceneIdPlayIdMap() {
        return RetrievedDocToSceneIdPlayIdMap;
    }

    public static void setRetrievedDocToSceneIdPlayIdMap(HashMap<Integer, String> retrievedDocToSceneIdPlayIdMap) {
        RetrievedDocToSceneIdPlayIdMap = retrievedDocToSceneIdPlayIdMap;
    }

    public void readLookUpTable(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.setRetrievedlookUpTable(mapper.readValue(new File(path + "lookUp.json"),
                    new TypeReference<HashMap<String, PostingListDisk>>() {
                    }));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public void readLookUpTable(boolean isCompressed){
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(!isCompressed)
                this.setRetrievedlookUpTable(mapper.readValue(new File(path + "lookUp.json"),
                            new TypeReference<HashMap<String, PostingListDisk>>() {}));
            else
                this.setRetrievedlookUpTable(mapper.readValue(new File(path + "lookUpCompressed.json"),
                        new TypeReference<HashMap<String, PostingListDisk>>() {}));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void decode ( byte [] input, ArrayList<Integer> output ) {
        for ( int i = 0; i < input.length; i++ ) {
            int position = 0;
            int result = ((int) input[i] & 0x7F);
            while ( ((input[i]) & 0x80) == 0 ) {
                i += 1;
                position += 1;
                int unsignedByte = ((int) input[i] & 0x7F);
                result |= (unsignedByte << (7 * position));
            }
            output.add(result);
        }
    }

    public ArrayList<Integer> getInvertedListForTerm(String term) throws IOException{

        ArrayList<Integer> termInvertedList = new ArrayList<>();

        PostingListDisk pObject = this.RetrievedLookUpTable.get(term);
        if(pObject == null){
            System.err.println("Term  "+term+ " Not found in File!");
            System.exit(1);
        }

        byte[] byteList = new byte[pObject.getLen()];
        reader.seek(pObject.getOffset());
        reader.read(byteList,0,pObject.getLen());
        if(!this.isCompressed()) {
            IntBuffer buffer = ByteBuffer.wrap(byteList).asIntBuffer();
                /* Write inverted List from buffer*/
            while (buffer.hasRemaining()) {
                termInvertedList.add(buffer.get());
            }
        }
        else{
            decode(byteList,termInvertedList);
            deltaDecode(termInvertedList);

        }


        return termInvertedList;

    }

    private void deltaDecode(ArrayList<Integer> arr){
        if(arr.size() == 0) return;
        int i = 0;
        int prevDocNum = 0;
        while(i<arr.size()){
            arr.set(i, arr.get(i++)+prevDocNum);
            prevDocNum = arr.get(i-1);
            int prevPosNum = 0;
            for(int j = i;j<i+arr.get(i);j++) {
                arr.set(j, arr.get(j++) + prevPosNum);
                prevPosNum = arr.get(j-1);
            }
            i= i+arr.get(i)+1;
        }
    }

    public PostingListDisk getPostingListDiskObjectForTerm(String term) throws IOException {
        PostingListDisk pObject = this.RetrievedLookUpTable.get(term);
        if(pObject == null){
            System.err.println("Term "+term+" Not found in File!");
            System.exit(1);
        }

        return pObject;
    }

    public String getHighestScoringTerm(String term) throws IOException{
        HashMap<String, Double> termToScoreMap = new HashMap<>();
        String highTerm = null;
        double maxScore = Integer.MIN_VALUE;
        ArrayList<Integer> invList = getInvertedListForTerm(term);
        int i = 0;
        while(i<invList.size()){
            int docNum = invList.get(i++);
            for(int j = i+1;j<=i+invList.get(i);j++){   //For every position in the document
                String[] docTerms = RetrievedDocToTermsMap.get(docNum);
                String prevTerm, nextTerm;
                int termPos = invList.get(j);
                if(termPos!=0){
                    prevTerm = docTerms[termPos-1];
                    if(!termToScoreMap.containsKey(prevTerm)){
                        termToScoreMap.put(prevTerm, getDiceCoeffScore(term,prevTerm,invList));
                    }
                }
                if(termPos!=docTerms.length-1){
                    nextTerm = docTerms[termPos+1];
                    if(!termToScoreMap.containsKey(nextTerm)){
                        termToScoreMap.put(nextTerm,getDiceCoeffScore(term,nextTerm,invList));
                    }
                }

            }
            i = i+invList.get(i)+1;  // move to next Document

        }

        // Return the term with highest score
        for(HashMap.Entry<String,Double> pair : termToScoreMap.entrySet()){
            if(maxScore<pair.getValue()){
                highTerm = pair.getKey();
                maxScore = pair.getValue();
            }
        }
        return highTerm;
    }

    public double getDiceCoeffScore(String term1, String term2, ArrayList<Integer> invList){
        double score = 0;
        int i = 0;
        int numWindows = 0, term1Count=0, term2Count;
        while(i<invList.size()){
            int docNum = invList.get(i++);
            for(int j = i+1;j<=i+invList.get(i);j++){
                int termPos = invList.get(j);
                String[] docTerms = RetrievedDocToTermsMap.get(docNum);
                if(termPos != 0 && docTerms[termPos-1].equals(term2)) numWindows++;
                if(termPos != docTerms.length-1 && docTerms[termPos+1].equals(term2)) numWindows++;

                if(termPos!=0 && termPos!=docTerms.length-1) term1Count += 2;
                else term1Count += 1;   //Edge Case. Add 1
            }
            i = i+invList.get(i)+1;  // move to next Document
        }
        try {
            /* Using approx. value for term2 only. Given the huge database, this should not affect much. */
            term2Count = getPostingListDiskObjectForTerm(term2).getTermFrequency() * 2;

            score = (double) numWindows/(double)(term1Count+term2Count);
        }catch (IOException e){
            e.printStackTrace();
        }
//        System.out.println("Score for terms :"+term1+","+term2+" is : "+score);

        return score;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static HashMap<String, PostingListDisk> getRetrievedlookUpTable() {
        return RetrievedLookUpTable;
    }

    public static void setRetrievedlookUpTable(HashMap<String, PostingListDisk> retrievedLookUpTable) {
        RetrievedLookUpTable = retrievedLookUpTable;
    }

    public static HashMap<String, ArrayList<Integer>> getRetrievedInvList() {
        return RetrievedInvList;
    }

    public static void setRetrievedInvList(HashMap<String, ArrayList<Integer>> retrievedInvList) {
        RetrievedInvList = retrievedInvList;
    }
}
