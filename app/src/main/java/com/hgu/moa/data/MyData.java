package com.hgu.moa.data;

import android.net.Uri;

public class MyData {
    public static String phoneNumber;
    public static String account;
    public static String accountName;
    public static String bankName;
    public static String accountNumber;
    public static String name;
    public static String mail;
    public static String uid;
    public static Uri photoUrl;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MyData.name = name;
    }

    public static String getMail() {
        return mail;
    }

    public static void setMail(String mail) {
        MyData.mail = mail;
    }

    public static Uri getPhotoUrl() {
        return photoUrl;
    }

    public static void setPhotoUrl(Uri photoUrl) {
        MyData.photoUrl = photoUrl;
    }

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        MyData.uid = uid;
    }
}
