package com.company;



import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

public class Evaluation {
    RetrievalAPI retrievalAPI;
    private String path;
    private boolean isCompressed;

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Evaluation(boolean isCompressed) throws IOException {
        this.retrievalAPI = new RetrievalAPI(isCompressed);
        path = "..//";
        this.setCompressed(isCompressed);
    }

    public void runEvaluation() throws IOException{

        System.out.println("Evaluation Started.........");
        ArrayList<PlayData> results;
        ArrayList<TermDocFrequency> tdf = new ArrayList<>();
        createQueryFiles();

        RandomAccessFile reader = new RandomAccessFile(new File(path+"oneWordQueries.txt"), "r");
        String query;
        try {
            while (true) {
                query = reader.readUTF();
                System.out.println(query);
                this.retrievalAPI.getFrequenciesForTerm(query, tdf);
                results = this.retrievalAPI.RetrieveQuery(query, 5);
                printResults(results);
            }
        }catch(EOFException e){
              //Do nothing
        }
    }
    private void createQueryFiles() throws IOException{
        RandomAccessFile oneWordQueries = new RandomAccessFile(new File(path+"oneWordQueries.txt"),"rw");
        RandomAccessFile twoWordQueries = new RandomAccessFile(new File(path+"twoWordQueries.txt"),"rw");
        int vocabSize = this.retrievalAPI.getVocabulary().size();
        Random random = new Random();
        for(int j = 0;j<100;j++) {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < 7; i++) {    // Generate 7 terms from vocab
                int vocabIndex = random.nextInt(vocabSize);

                String queryTerm = this.retrievalAPI.getVocabulary().get(vocabIndex);
                query.append(queryTerm);
                if (i != 6) query.append(" ");


            }
            oneWordQueries.writeUTF(query.toString() + "\n");
            String twoWordQuery = this.retrievalAPI.getHighestScoringPhrase(query.toString());
            twoWordQueries.writeUTF(twoWordQuery+"\n");
        }
        oneWordQueries.close();
        twoWordQueries.close();
    }

    public  void printResults(ArrayList<PlayData> results){
        System.out.println("Query Results");
        for(PlayData res : results){
            System.out.println(res.getSceneId());
        }

    }


}
