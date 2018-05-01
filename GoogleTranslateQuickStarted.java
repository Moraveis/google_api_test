/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joao.getsubtitled;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author joao
 */
public class GoogleTranslateQuickStarted {

    private static String KEY_FILE_LOCATION = "key.json";
    private static String SUBTITLE_FILE_LOCATION = "in-file.srt"; // .txt or whatever
    private static String TRANSLATED_FILE_LOCATION = "out-file.srt"; // .txt ot whatever

    public static void main(String[] args) {
        try {
            TranslateOptions.Builder optionsBuilder = TranslateOptions.newBuilder();
            optionsBuilder.setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(KEY_FILE_LOCATION)));
            optionsBuilder.setTargetLanguage("pt");

            Translate translate = optionsBuilder.build().getService();
            String traducao = translate.translate(getTextFromFile(SUBTITLE_FILE_LOCATION)).getTranslatedText();
            System.out.println(traducao);
            FileWriter fw = new FileWriter(new File(TRANSLATED_FILE_LOCATION));
            fw.write(traducao);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTextFromFile(String filename) {
        String legenda = "";
        BufferedReader br = null;
        FileReader fr = null;
        try {
            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                legenda += sCurrentLine;
            }
            return legenda;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
