package com.example.mp3player;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton Class: Manages User Preferences
 * This class provides methods to get and set user preferences for the MP3 player application,
 * including background color and playback speed.
 */
public class AppPreferences {

    /**
     * Key for storing and retrieving background color preference in SharedPreferences.
     */
    public static final String BACKGROUND_COLOR_KEY = "background_colour";

    /**
     * Key for storing and retrieving playback speed preference in SharedPreferences.
     */
    public static final String PLAYBACK_SPEED_KEY = "playback_speed";

    private final Context context;
    private static AppPreferences instance;

    /**
     * Private constructor for Singleton pattern.
     *
     * @param context The application context used for accessing SharedPreferences.
     */
    private AppPreferences(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Retrieves the singleton instance of AppPreferences. Creates a new instance if it doesn't exist.
     *
     * @param context The application context used for accessing SharedPreferences.
     * @return The AppPreferences instance.
     */
    public static AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    /**
     * Gets the stored background color preference.
     *
     * @return The background color preference.
     */
    public int getBackgroundColour() {
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.BACKGROUND_COLOR_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(BACKGROUND_COLOR_KEY, 0);
    }

    /**
     * Sets the background color preference.
     *
     * @param colour The background color to set.
     */
    public void setBackgroundColor(int colour) {
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.BACKGROUND_COLOR_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BACKGROUND_COLOR_KEY, colour);
        editor.apply();
    }

    /**
     * Gets the stored playback speed preference.
     *
     * @return The playback speed preference.
     */
    public float getPlaybackSpeed() {
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.PLAYBACK_SPEED_KEY, Context.MODE_PRIVATE);
        return preferences.getFloat(PLAYBACK_SPEED_KEY, 1);
    }

    /**
     * Sets the playback speed preference.
     *
     * @param speed The playback speed to set.
     */
    public void setPlaybackSpeed(float speed) {
        SharedPreferences preferences = context.getSharedPreferences(AppPreferences.PLAYBACK_SPEED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PLAYBACK_SPEED_KEY, speed);
        editor.apply();
    }

}