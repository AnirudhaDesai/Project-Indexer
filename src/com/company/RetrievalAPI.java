package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RetrievalAPI {
    DiskReader diskReader;
    ArrayList<PlayData> RetrievalResults;
    private ArrayList<String> vocabulary;

    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(ArrayList<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public RetrievalAPI() {
        diskReader = new DiskReader();
        vocabulary = new ArrayList<>();
        buildVocabulary();
    }
    /* Overloaded Constructor for compressed case*/
    public RetrievalAPI(boolean isCompressed) throws IOException{
        diskReader = new DiskReader(isCompressed);
        vocabulary = new ArrayList<>();
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
            System.out.printf("The term frequency, DocFrequency for %s is : %d,%d \n",word,
                    freqObject.getDocFrequency(),freqObject.getDocFrequency());
        }

    }

    public ArrayList<PlayData> RetrieveQuery(String query) {
       RetrievalResults  = new ArrayList<>();

        return RetrievalResults;
    }



}
