package com.app.bicoccajobs.utils;

import android.content.Context;
import android.content.SharedPreferences;

//the main purpose of this class to create an instance of SharedPreferences(a local phone storage store small amount of data temporarily, save data
// data to SharedPreferences and remove data from SharedPreferences'
public class SharedPref {
    private static SharedPref instance;
    public static String PREFERENCE = "AppData";
    private Context ctx;
    private SharedPreferences sharedPreferences;
    private String USER = "user";

    public SharedPref(Context context) {
        this.ctx = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCE, 0);
    }

    //this method reate an instance of SharedPreferences
    public static SharedPref getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPref(context);
        }
        return instance;
    }

    // this method update data from SharedPreferences
    public void setUSER(String json) {
        sharedPreferences.edit().putString(USER, json).apply();
    }
    public static void setShop(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("statusShop",true).apply();
    }

    public static boolean getShop(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
        boolean statusShop = sharedPref.getBoolean("statusShop", false);
        return statusShop;
    }

    public static void setStudent(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("studentInfo", Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("statusStudent",true).apply();
    }
    public static boolean getStudent(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("studentInfo", Context.MODE_PRIVATE);
        boolean statusShop = sharedPref.getBoolean("statusStudent", false);
        return statusShop;
    }

    public String getUSER() {
        return sharedPreferences.getString(USER, null);
    }
}
