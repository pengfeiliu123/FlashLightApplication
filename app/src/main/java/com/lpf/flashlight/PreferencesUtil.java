package com.lpf.flashlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

public class PreferencesUtil {

    private static PreferencesUtil instance;

    private static SharedPreferences sharedPreferences;

    private PreferencesUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesUtil getInstance(Context context) {
        if (instance == null)
            instance = new PreferencesUtil(context);
        return instance;
    }

    public int savedBgColor(int bgColor){
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("bgColor", bgColor);
        }
        return R.color.black;
    }

    public int getBgColor(){
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("bgColor", R.color.black);
        }
        return R.color.black;
    }
}
