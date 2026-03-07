package com.ibscc.bible;

import android.content.Context;
import org.json.*;
import java.io.*;
import java.util.*;

public class BibleHelper {

    private JSONArray bible;
    private Context context;

    public BibleHelper(Context context) {
        this.context = context;
        loadBible();
    }

    private void loadBible() {
        try {
            InputStream is = context.getAssets().open("ACF.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            bible = new JSONArray(json);
        } catch (Exception e) {
            bible = new JSONArray();
            e.printStackTrace();
        }
    }

    public List<String> getBookNames() {
        List<String> books = new ArrayList<>();
        try {
            for (int i = 0; i < bible.length(); i++) {
                JSONObject book = bible.getJSONObject(i);
                String abbrev = book.getString("abbrev");
                String name = getBookName(abbrev);
                if (name != null) {
                    books.add(name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<String> getBookAbbreviations() {
        List<String> abbrs = new ArrayList<>();
        try {
            for (int i = 0; i < bible.length(); i++) {
                JSONObject book = bible.getJSONObject(i);
                String abbrev = book.getString("abbrev");
                abbrs.add(abbrev);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return abbrs;
    }

    public int getChapterCount(String bookAbbr) {
        try {
            for (int i = 0; i < bible.length(); i++) {
                JSONObject book = bible.getJSONObject(i);
                if (book.getString("abbrev").equals(bookAbbr)) {
                    JSONArray chapters = book.getJSONArray("chapters");
                    return chapters.length();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Verse> getChapter(String bookAbbr, int chapter) {
        List<Verse> verses = new ArrayList<>();
        try {
            for (int i = 0; i < bible.length(); i++) {
                JSONObject book = bible.getJSONObject(i);
                if (book.getString("abbrev").equals(bookAbbr)) {
                    JSONArray chapters = book.getJSONArray("chapters");
                    JSONArray chapterVerses = chapters.getJSONArray(chapter - 1);
                    
                    for (int v = 0; v < chapterVerses.length(); v++) {
                        Verse verse = new Verse();
                        verse.setNumber(v + 1);
                        verse.setText(chapterVerses.getString(v));
                        verses.add(verse);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return verses;
    }

    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.length() < 3) return results;
        
        query = query.toLowerCase();
        int count = 0;

        try {
            for (int i = 0; i < bible.length(); i++) {
                JSONObject book = bible.getJSONObject(i);
                String bookName = getBookName(book.getString("abbrev"));
                JSONArray chapters = book.getJSONArray("chapters");

                for (int c = 0; c < chapters.length(); c++) {
                    JSONArray verses = chapters.getJSONArray(c);

                    for (int v = 0; v < verses.length(); v++) {
                        String text = verses.getString(v);
                        
                        if (text.toLowerCase().contains(query)) {
                            results.add(bookName + " " + (c + 1) + ":" + (v + 1) + "\n" + text);
                            count++;
                            if (count >= 50) return results;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Converte nome completo para abreviação
    public String getBookAbbreviation(String bookName) {
        switch (bookName) {
            case "Gênesis": return "gn";
            case "Êxodo": return "ex";
            case "Levítico": return "lv";
            case "Números": return "nm";
            case "Deuteronômio": return "dt";
            case "Josué": return "js";
            case "Juízes": return "jz";
            case "Rute": return "rt";
            case "1 Samuel": return "1sm";
            case "2 Samuel": return "2sm";
            case "1 Reis": return "1rs";
            case "2 Reis": return "2rs";
            case "1 Crônicas": return "1cr";
            case "2 Crônicas": return "2cr";
            case "Esdras": return "ed";
            case "Neemias": return "ne";
            case "Ester": return "et";
            case "Jó": return "job";
            case "Salmos": return "sl";
            case "Provérbios": return "pv";
            case "Eclesiastes": return "ec";
            case "Cânticos": return "ct";
            case "Isaías": return "is";
            case "Jeremias": return "jr";
            case "Lamentações": return "lm";
            case "Ezequiel": return "ez";
            case "Daniel": return "dn";
            case "Oséias": return "os";
            case "Joel": return "jl";
            case "Amós": return "am";
            case "Obadias": return "ob";
            case "Jonas": return "jn";
            case "Miquéias": return "mq";
            case "Naum": return "na";
            case "Habacuque": return "hc";
            case "Sofonias": return "sf";
            case "Ageu": return "ag";
            case "Zacarias": return "zc";
            case "Malaquias": return "ml";
            case "Mateus": return "mt";
            case "Marcos": return "mc";
            case "Lucas": return "lc";
            case "João": return "jo";
            case "Atos": return "at";
            case "Romanos": return "rm";
            case "1 Coríntios": return "1co";
            case "2 Coríntios": return "2co";
            case "Gálatas": return "gl";
            case "Efésios": return "ef";
            case "Filipenses": return "fp";
            case "Colossenses": return "cl";
            case "1 Tessalonicenses": return "1ts";
            case "2 Tessalonicenses": return "2ts";
            case "1 Timóteo": return "1tm";
            case "2 Timóteo": return "2tm";
            case "Tito": return "tt";
            case "Filemom": return "fm";
            case "Hebreus": return "hb";
            case "Tiago": return "tg";
            case "1 Pedro": return "1pe";
            case "2 Pedro": return "2pe";
            case "1 João": return "1jo";
            case "2 João": return "2jo";
            case "3 João": return "3jo";
            case "Judas": return "jd";
            case "Apocalipse": return "ap";
            default: return bookName.toLowerCase();
        }
    }

    // Converte abreviação para nome completo
    public String getBookName(String abbr) {
        switch (abbr) {
            case "gn": return "Gênesis";
            case "ex": return "Êxodo";
            case "lv": return "Levítico";
            case "nm": return "Números";
            case "dt": return "Deuteronômio";
            case "js": return "Josué";
            case "jz": return "Juízes";
            case "rt": return "Rute";
            case "1sm": return "1 Samuel";
            case "2sm": return "2 Samuel";
            case "1rs": return "1 Reis";
            case "2rs": return "2 Reis";
            case "1cr": return "1 Crônicas";
            case "2cr": return "2 Crônicas";
            case "ed": return "Esdras";
            case "ne": return "Neemias";
            case "et": return "Ester";
            case "job": return "Jó";
            case "sl": return "Salmos";
            case "pv": return "Provérbios";
            case "ec": return "Eclesiastes";
            case "ct": return "Cânticos";
            case "is": return "Isaías";
            case "jr": return "Jeremias";
            case "lm": return "Lamentações";
            case "ez": return "Ezequiel";
            case "dn": return "Daniel";
            case "os": return "Oséias";
            case "jl": return "Joel";
            case "am": return "Amós";
            case "ob": return "Obadias";
            case "jn": return "Jonas";
            case "mq": return "Miquéias";
            case "na": return "Naum";
            case "hc": return "Habacuque";
            case "sf": return "Sofonias";
            case "ag": return "Ageu";
            case "zc": return "Zacarias";
            case "ml": return "Malaquias";
            case "mt": return "Mateus";
            case "mc": return "Marcos";
            case "lc": return "Lucas";
            case "jo": return "João";
            case "at": return "Atos";
            case "rm": return "Romanos";
            case "1co": return "1 Coríntios";
            case "2co": return "2 Coríntios";
            case "gl": return "Gálatas";
            case "ef": return "Efésios";
            case "fp": return "Filipenses";
            case "cl": return "Colossenses";
            case "1ts": return "1 Tessalonicenses";
            case "2ts": return "2 Tessalonicenses";
            case "1tm": return "1 Timóteo";
            case "2tm": return "2 Timóteo";
            case "tt": return "Tito";
            case "fm": return "Filemom";
            case "hb": return "Hebreus";
            case "tg": return "Tiago";
            case "1pe": return "1 Pedro";
            case "2pe": return "2 Pedro";
            case "1jo": return "1 João";
            case "2jo": return "2 João";
            case "3jo": return "3 João";
            case "jd": return "Judas";
            case "ap": return "Apocalipse";
            default: return abbr;
        }
    }
}