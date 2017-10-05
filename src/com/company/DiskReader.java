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
    private static HashMap<String, Integer> RetrievedSceneIdToDocMap;
    RandomAccessFile reader;

    public DiskReader() {
        setPath("..//");
        RetrievedLookUpTable = new HashMap<>();
        RetrievedInvList = new HashMap<>();
        RetrievedDocToSceneIdMap = new HashMap<>();
        RetrievedDocToSceneMap = new HashMap<>();
        RetrievedSceneIdToDocMap = new HashMap<>();
        try {
            reader = new RandomAccessFile(path + "InvertedList.dat", "r");
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    /* Overloaded Constructor to read if using Compressed Index */
    public DiskReader (boolean isCompressed) throws IOException {
        setPath("..//");
        RetrievedLookUpTable = new HashMap<>();
        RetrievedInvList = new HashMap<>();
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
    }

    public void readInvListFromDisk() throws IOException{
        readLookUpTable();   /* Retrieves the lookUpTable with Term->Offset mappings from disk */

        for(HashMap.Entry<String, PostingListDisk> pair : this.RetrievedLookUpTable.entrySet()){
            ArrayList<Integer> termInvertedList = new ArrayList<>();
            String term = pair.getKey();
            PostingListDisk pObject = pair.getValue();
//            reader.seek(pObject.getOffset());
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
//            reader.seek(pObject.getOffset());
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
