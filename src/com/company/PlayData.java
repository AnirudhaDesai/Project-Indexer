package com.company;

public class PlayData {
    private static String playId;
    private static String sceneId;
    private static Long sceneNum;
    private static String text;

    public static String getPlayId() {
        return playId;
    }

    public static void setPlayId(String playId) {
        PlayData.playId = playId;
    }

    public static String getSceneId() {
        return sceneId;
    }

    public static void setSceneId(String sceneId) {
        PlayData.sceneId = sceneId;
    }

    public static Long getSceneNum() {
        return sceneNum;
    }

    public static void setSceneNum(Long sceneNum) {
        PlayData.sceneNum = sceneNum;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        PlayData.text = text;
    }
}
