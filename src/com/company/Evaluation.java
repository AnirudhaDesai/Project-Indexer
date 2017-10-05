package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Evaluation {
    RetrievalAPI retrievalAPI;

    public Evaluation() {
        this.retrievalAPI = new RetrievalAPI();
    }

    public void runEvaluation() throws IOException{
        /* Randomly Select 7 terms */
        System.out.println("Evaluation Started.........");
        int vocabSize = this.retrievalAPI.getVocabulary().size();
        StringBuilder query = new StringBuilder();
        ArrayList<TermDocFrequency> tdf = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i<7;i++){    // Generate 7 terms from vocab
            int vocabIndex = random.nextInt(vocabSize);

            String queryTerm = this.retrievalAPI.getVocabulary().get(vocabIndex);
            query.append(queryTerm);
            if(i!=6) query.append(" ");


        }
        System.out.println("The query built is : "+ query.toString());
        this.retrievalAPI.getFrequenciesForTerm(query.toString(), tdf);




    }
}
