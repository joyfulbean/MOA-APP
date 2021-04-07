package com.handong.moa.data;

public class ServerInfo {
    private static String ip = "3.19.66.183";
    private static String port = "5000";

    public static String getUrl(){
        return "http://" + ip + ":" + port + "/";
    }
}
