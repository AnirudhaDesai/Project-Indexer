package com.company;

import java.util.ArrayList;

/* Document Posting Class */
public class Posting {

    Long DocId;    // Document Id
    Long termFreq;  // Term Frequency
    ArrayList<Long> pos;  // List of Positions in the Document

    public Posting(Long docId, Long termFreq) {
        DocId = docId;
        this.termFreq = termFreq;
    }

    public Posting() {

    }

    public Long getDocId() {

        return DocId;
    }

    public void setDocId(Long docId) {
        DocId = docId;
    }

    public Long getTermFreq() {
        return termFreq;
    }

    public void setTermFreq(Long termFreq) {
        this.termFreq = termFreq;
    }

    public ArrayList<Long> getPos() {
        return pos;
    }

    public void setPos(ArrayList<Long> pos) {
        this.pos = pos;
    }
}
