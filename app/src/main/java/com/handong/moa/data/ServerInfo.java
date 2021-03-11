package com.handong.moa.data;

public class ServerInfo {
    private static String ip = "13.209.77.6";
    private static String port = "5000";

    public static String getUrl(){
        return "http://" + ip + ":" + port + "/";
    }
}
