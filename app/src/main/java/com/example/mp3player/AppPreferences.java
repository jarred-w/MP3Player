package com.example.mp3player;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *
 * Singleton Class: Manages User Preferences
**/
public class AppPreferences {
    public static final String BACKGROUND_COLOR_KEY = "background_colour";
    public static final String PLAYBACK_SPEED_KEY = "playback_speed";
    private final Context context;
    private static AppPreferences instance;

    //Private constructor for Singleton
    private AppPreferences(Context context) {
        this.context = context.getApplicationContext();
    }

    //Used when accessing class, makes new Object of class if no Object has been made
    public static AppPreferences getInstance(Context context){
        if(instance == null){
            instance = new AppPreferences(context);
        }
        return instance;
    }

    //Gets background colour
    public int getBackgroundColour(){
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.BACKGROUND_COLOR_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(BACKGROUND_COLOR_KEY, 0);
    }

    //Sets background colour
    public void setBackgroundColor(int colour){
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.BACKGROUND_COLOR_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BACKGROUND_COLOR_KEY, colour);
        editor.apply();
    }

    //Gets playback speed
    public float getPlaybackSpeed(){
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.PLAYBACK_SPEED_KEY, Context.MODE_PRIVATE);
        return preferences.getFloat(PLAYBACK_SPEED_KEY, 1);
    }

    //Sets playback speed
    public void setPlaybackSpeed(float speed) {
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.PLAYBACK_SPEED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PLAYBACK_SPEED_KEY, speed);
        editor.apply();
    }

}