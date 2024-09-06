package com.example.fetch;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class ExpandableListDataPump {
    public static TreeMap<String, ArrayList<Info>> getData() throws JSONException, IOException {
        TreeMap<String, ArrayList<Info>> expandableListDetail = new TreeMap<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String json = readJsonFromUrl("https://fetch-hiring.s3.amazonaws.com/hiring.json");
        JSONArray jsonArr = new JSONArray(json);
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            Item item = new Item();
            String listId =  jsonObj.getString("listId");
            String id = jsonObj.getString("id");
            String name = jsonObj.getString("name");
            name = name.replaceAll("[^\\d.]", "");
            item.setId(id);
            if (!name.isEmpty() && !name.equals("null")) {
                item.setName(name);
                ArrayList<Info> list = expandableListDetail.getOrDefault(listId, new ArrayList<>());
                list.add(new Info(id,name));
                expandableListDetail.put(listId,list);
            }
        }
        for (String s : expandableListDetail.keySet()) {
            ArrayList<Info> list = expandableListDetail.get(s);
            list.sort((a,b) -> Integer.parseInt(a.getName()) - (Integer.parseInt(b.getName())));
            expandableListDetail.put(s,list);
        }
        return expandableListDetail;
    }

    public static String readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return readAll(rd);
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}