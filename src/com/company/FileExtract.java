package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class FileExtract {
    private static final String data_path = "D:/Fall 2017/Information Retrieval/Assignment1/shakespeare-scenes.json.gz";
    private static final String outdata_path = "D:/Fall 2017/Information Retrieval/Assignment1/shakespeare-scenes.json";
    public static void unzip(String[] args){
        FileExtract gZip = new FileExtract();
        gZip.gunzipIt();

    }
    public static void gunzipIt(){
        byte[] buffer = new byte[1024];
        try{
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(data_path));
            FileOutputStream out = new FileOutputStream(outdata_path);
            int len;
            while((len = gzis.read(buffer)) > 0){
                out.write(buffer,0,len);

            }
            gzis.close();
            out.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public static  ArrayList<PlayData> readJSON(){
    /* Function to parse json file from outdata_path and store
            return an ArrayList of JSON Objects.
     */

        ArrayList<PlayData> playObj = new ArrayList<PlayData>();

        JSONObject obj;

        try{
            FileReader fileReader = new FileReader(outdata_path);
//
            obj = (JSONObject)new JSONParser().parse(fileReader);
//        System.out.print("JSON Object : "+obj);
            JSONArray jArray = (JSONArray)obj.get("corpus");
//        System.out.println("JSON Array : "+jArray);
            for(int i = 0;i<jArray.size();i++){
                obj = (JSONObject) jArray.get(i);
                PlayData pObj = new PlayData();
                pObj.setPlayId((String) obj.get("playId"));
                pObj.setSceneId((String) obj.get("sceneId"));
                pObj.setSceneNum((Long) obj.get("sceneNum"));
                pObj.setText((String) obj.get("text"));

//                System.out.println(pObj.getText());
                if(!playObj.add(pObj)) System.out.println("Error in Adding object");
//                System.out.println(playObj.get(i).getText());

            }

            System.out.println("JSON file parsing successful with size : "+playObj.size());


        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return playObj;
    }



}
