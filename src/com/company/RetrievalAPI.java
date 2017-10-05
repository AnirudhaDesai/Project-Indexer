package com.company;

import java.io.IOException;
import java.util.ArrayList;

public class RetrievalAPI {
    DiskReader diskReader;
    ArrayList<PlayData> RetrievalResults;

    public RetrievalAPI() {
        diskReader = new DiskReader();
    }
    public RetrievalAPI(boolean isCompressed) throws IOException{
        diskReader = new DiskReader(isCompressed);
    }

    public ArrayList<PlayData> RetrieveQuery(String query) {
       RetrievalResults  = new ArrayList<>();

        return RetrievalResults;
    }



}
