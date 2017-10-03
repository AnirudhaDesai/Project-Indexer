package com.company;

import java.util.ArrayList;

public class ListOfScenes {

    private static ArrayList<PlayData> playObjects;
    public void ListOfScenes(){
        playObjects = new ArrayList<PlayData>();
    }

    public static boolean addData(PlayData obj){

        if(!playObjects.add(obj)) return false;
        return true;
    }

    public static ArrayList<PlayData> getPlayObjects(){
        return playObjects;
    }

    public static void setPlayObjects(ArrayList<PlayData> obj){
        playObjects = obj;
    }

    public static int getSize(){
        return playObjects.size();
    }

}
