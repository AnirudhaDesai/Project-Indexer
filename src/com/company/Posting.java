package com.company;

import java.util.ArrayList;

/* Document Posting Class */
public class Posting {

    Integer DocId;    // Document Id
    Integer termFreq;  // Term Frequency
    ArrayList<Integer> pos;  // List of Positions in the Document

    public Posting(Integer docId, Integer termFreq) {
        DocId = docId;
        this.termFreq = termFreq;
    }

    public Posting() {

    }

    public Integer getDocId() {

        return DocId;
    }

    public void setDocId(Integer docId) {
        DocId = docId;
    }

    public Integer getTermFreq() {
        return termFreq;
    }

    public void setTermFreq(int termFreq) {
        this.termFreq = termFreq;
    }

    public ArrayList<Integer> getPos() {
        return pos;
    }

    public void setPos(ArrayList<Integer> pos) {
        this.pos = pos;
    }
}
