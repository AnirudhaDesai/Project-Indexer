package com.company;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class DiskWriter {
private RandomAccessFile InvertedList;
private RandomAccessFile InvertedListCompressed;

private static HashMap<String, PostingListDisk> lookUpTable;  /*Maps index Terms to PostingListDisk Object */

    public DiskWriter(boolean isCompressed) {
        try {
            if(!isCompressed)
                InvertedList = new RandomAccessFile(new File("..//InvertedList.dat"), "rw");
            else
                InvertedListCompressed = new RandomAccessFile(new File("..//InvertedListCompressed.dat"), "rw");

        }catch(IOException e){
            e.printStackTrace();
        }

        lookUpTable = new HashMap<>();

    }

    public void writeInvIndexToFile(InvertedIndex invPostings) throws IOException{
        /* Input -> Inverted Index Object
        Output -> Writes a binary file to disk. Updates lookUp Table. Writes LookUpTable to disk.
         */
        int totalBytes = 0;
        for(HashMap.Entry<String, ArrayList<Integer>> postingList : invPostings.getInvIndex().entrySet()){
            String word = postingList.getKey();
//            System.out.println("The word is : " + word);
            ArrayList<Integer> pList = postingList.getValue();
            PostingListDisk object = new PostingListDisk();
            object.setOffset(InvertedList.getFilePointer());
            int i = 0, df = 0, tf = 0;
            int byteLength = 0;

            while(i<pList.size()){
                int docid = pList.get(i++);
                InvertedList.writeInt(docid);
                df += 1;   // Increment doc frequency
                int tfi = pList.get(i++);
//                System.out.println("For DocID : "+docid+ "Term Frequency : "+tfi);
                InvertedList.writeInt(tfi);
                tf += tfi;

                byteLength += 2*(Integer.SIZE/Byte.SIZE);

                for(int j = i;j<i+tfi;j++){
                    InvertedList.writeInt(pList.get(j));
                    byteLength += (Integer.SIZE/Byte.SIZE);
                }
                i = i+tfi;

            }
            /* The posting List for the term is written to file. Consolidate data */
            object.setTermFrequency(tf);
//            System.out.println("The total tf for : "+word+ " is : "+tf + "df :" + df);
            object.setDocFrequency(df);
            object.setLen(byteLength);
            totalBytes += byteLength;
//            System.out.println("Uncompressed Size: " + byteLength);

            lookUpTable.put(word,object);
            }
        System.out.println("Total Uncompressed : "+ totalBytes);
        InvertedList.close();
        /* Write LookUpTable to disk */
        writeLookUpToDisk(false);


    }

    public void writeCompressedInvIndexToFile(InvertedIndex invPostings) throws IOException {

        /* Input -> Inverted Index Object with delta Encoded Values
        Output -> Writes a binary file to disk. Updates lookUp Table. Writes LookUpTable to disk.
         */
        int totalBytes = 0;
        for(HashMap.Entry<String, ArrayList<Integer>> postingList : invPostings.getInvIndex().entrySet()) {
            String word = postingList.getKey();
//            System.out.println("The word is : " + word);
            ArrayList<Integer> pList = postingList.getValue();
            PostingListDisk object = new PostingListDisk();
            object.setOffset(InvertedListCompressed.getFilePointer());
            int i = 0, df = 0, tf = 0;
            int byteLength = 0;

            ByteBuffer encodedPList = ByteBuffer.allocate(4*pList.size()); // Initialize with max Possible. Truncate Later.
            encode(pList, encodedPList);
            encodedPList.flip();
            byteLength = encodedPList.limit();  // Can use remaining() method as well.
            byte[] finalEncodedPList = new byte[encodedPList.limit()];
            encodedPList.get(finalEncodedPList,encodedPList.position(),encodedPList.limit());

            InvertedListCompressed.write(finalEncodedPList);  // Write to file

            while(i<pList.size()) {
                int docid = pList.get(i++);
                df += 1;   // Increment doc frequency
                int tfi = pList.get(i++);
                tf += tfi;
                i += tfi;
            }

            object.setTermFrequency(tf);

            object.setDocFrequency(df);
            object.setLen(byteLength);
            totalBytes += byteLength;

            lookUpTable.put(word, object);

        }
        System.out.println("Total Compressed : "+ totalBytes);

        InvertedListCompressed.close();
        writeLookUpToDisk(true);

        }
    private void writeLookUpToDisk(boolean isCompressed){
        ObjectMapper mapper = new ObjectMapper();
        String path;
        if(!isCompressed)
            path = "..//lookUp.json";
        else
            path = "..//lookUpCompressed.json";

        try {
            mapper.writeValue(new File(path), lookUpTable);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void encode ( ArrayList<Integer> input, ByteBuffer output) {
        for (int i : input ) {
            while ( i >= 128 ) {
                output.put ( (byte) (i & 0x7F) ) ;
                i >>>= 7 ; // logical shift, no sign bit extension
            }
            output.put((byte) (i | 0x80) );
        }
    }




}
