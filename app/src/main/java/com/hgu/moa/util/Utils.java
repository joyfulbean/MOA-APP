package com.hgu.moa.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //Todo: #, //, ;,
    public static boolean isValidInput(String userInput){

        Log.i("isSql", "user input:" + userInput);
        Log.i("isSql","Log 1");
        if(isSpecialChars(userInput)) return false;
        Log.i("isSql","Log 2");
        if(isSqlQuery(userInput)) return false;
        Log.i("isSql","Log 3");

        return true;
    }

    private static boolean isSpecialChars(String userInput){
        final String regex = "['\"#;/]";

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(userInput);

        if(matcher.find()){
            return true;
        } else
            return false;
    }

    private static boolean isSqlQuery(String userInput){
        Log.i("isSql", "Starting to chekc is sal");
        final String regex = "(union|select|from|where)";

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(userInput);

        if(matcher.find()){
            Log.i("isSql", "It's SQL INJECTION!!!");
            return true;
        } else {
            Log.i("isSql", "It is not SQL INJECTION!!!");
            return false;
        }
    }
}
