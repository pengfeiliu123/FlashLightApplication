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

    public boolean savedBgColor(int bgColor) {
        if (sharedPreferences != null) {
            return sharedPreferences.edit().putInt("bgColor", bgColor).commit();
        }
        return false;
    }

    public int getBgColor() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("bgColor", R.color.black);
        }
        return R.color.black;
    }

    public void saveLightWidth(int width) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt("width", width).commit();
        }
    }

    public void saveLightHeight(int height) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt("height", height).commit();
        }
    }

    public int getLightWidth() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("width", 200);
        }
        return R.color.black;
    }

    public int getLightHeight() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("height", 200);
        }
        return R.color.black;
    }
}
