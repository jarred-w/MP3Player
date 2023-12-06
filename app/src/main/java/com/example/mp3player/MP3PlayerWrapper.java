package com.example.mp3player;

/**
 * Wrapper class for MP3Player class.
 */
public class MP3PlayerWrapper {

    // Singleton instance of the MP3PlayerWrapper.
    private static MP3PlayerWrapper instance;

    // Instance of the underlying MP3Player.
    private final MP3Player mp3Player;

    /**
     * Private constructor for Singleton pattern.
     */
    private MP3PlayerWrapper() {
        this.mp3Player = new MP3Player();
    }

    /**
     * Creates an instance of MP3PlayerWrapper if null, else returns the existing instance.
     *
     * @return The instance of MP3PlayerWrapper.
     */
    public static synchronized MP3PlayerWrapper getInstance() {
        if (instance == null) {
            instance = new MP3PlayerWrapper();
        }
        return instance;
    }

    /**
     * Wrapper method for playing audio using MP3Player.
     */
    public void play() {
        mp3Player.play();
    }

    /**
     * Wrapper method for loading an audio file using MP3Player.
     *
     * @param filePath       The file path of the audio file.
     * @param playbackSpeed  The playback speed.
     */
    public void load(String filePath, float playbackSpeed) {
        mp3Player.load(filePath, playbackSpeed);
    }

    /**
     * Wrapper method for pausing audio playback using MP3Player.
     */
    public void pause() {
        mp3Player.pause();
    }

    /**
     * Wrapper method for stopping audio playback using MP3Player.
     */
    public void stop() {
        mp3Player.stop();
    }

    /**
     * Wrapper method for getting the duration of the loaded audio file using MP3Player.
     *
     * @return The duration of the audio file in milliseconds.
     */
    public int getDuration() {
        return mp3Player.getDuration();
    }

    /**
     * Wrapper method for getting the progress of audio playback using MP3Player.
     *
     * @return The progress of audio playback in milliseconds.
     */
    public int getProgress() {
        return mp3Player.getProgress();
    }

    /**
     * Wrapper method for getting the file path of the loaded audio file using MP3Player.
     *
     * @return The file path of the loaded audio file.
     */
    public String getFilePath() {
        return mp3Player.getFilePath();
    }

    /**
     * Wrapper method for getting the current state of MP3Player.
     *
     * @return The current state of MP3Player.
     */
    public MP3Player.MP3PlayerState getState() {
        return mp3Player.getState();
    }

    /**
     * Wrapper method for setting the playback speed using MP3Player.
     *
     * @param speed The playback speed to set.
     */
    public void setPlaybackSpeed(float speed) {
        mp3Player.setPlaybackSpeed(speed);
    }
}