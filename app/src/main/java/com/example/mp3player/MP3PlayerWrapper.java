package com.example.mp3player;

/**
 *
 * Wrapper class for MP3Player class
 */
public class MP3PlayerWrapper {
    private static MP3PlayerWrapper instance;
    private final MP3Player mp3Player;

    //Private constructor for Singleton
    private MP3PlayerWrapper() {
        this.mp3Player = new MP3Player();
    }

    //Creates instance if null, else returns instance
    public static synchronized MP3PlayerWrapper getInstance() {
        if (instance == null) {
            instance = new MP3PlayerWrapper();
        }
        return instance;
    }

    //Wrapper for MP3Player class
    public void play() {
        mp3Player.play();
    }

    //Wrapper for MP3Player class
    public void load(String filePath, float playbackSpeed) {
        mp3Player.load(filePath, playbackSpeed);
    }

    //Wrapper for MP3Player class
    public void pause() {
        mp3Player.pause();
    }

    //Wrapper for MP3Player class
    public void stop() {
        mp3Player.stop();
    }

    //Wrapper for MP3Player class
    public int getDuration(){
        return mp3Player.getDuration();
    }

    //Wrapper for MP3Player class
    public int getProgress(){
        return mp3Player.getProgress();
    }

    //Wrapper for MP3Player class
    public String getFilePath() {
        return mp3Player.getFilePath();
    }

    //Wrapper for MP3Player class
    public MP3Player.MP3PlayerState getState() {
        return mp3Player.getState();
    }

    //Wrapper for MP3Player class
    public void setPlaybackSpeed(float speed) {
        mp3Player.setPlaybackSpeed(speed);
    }
}
