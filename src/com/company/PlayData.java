package com.company;

public class PlayData {
    private  String playId;
    private  String sceneId;
    private  Long sceneNum;
    private  String text;

    public PlayData() {
    }

    public PlayData(String playId) {
        this.playId = playId;
    }

    public String getPlayId() {
        return this.playId;
    }

    public  void setPlayId(String playId) {
        this.playId = playId;
    }

    public  String getSceneId() {
        return this.sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public  Long getSceneNum() {
        return this.sceneNum;
    }

    public  void setSceneNum(Long sceneNum) {
        this.sceneNum = sceneNum;
    }

    public  String getText() {
        return this.text;
    }

    public  void setText(String text) {
        this.text = text;
    }
}
