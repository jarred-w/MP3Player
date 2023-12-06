package com.example.mp3player;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Service class for managing music playback in the background.
 */
public class MusicService extends Service {

    // Notification ID for the foreground service
    private static final int NOTIFICATION_ID = 1;

    // Playback speed of the music
    private float playbackSpeed;

    // Wrapper class for the MP3Player
    private MP3PlayerWrapper mp3Wrapper;

    // Flag indicating whether to resume playback
    private Boolean resume = false;

    // Local class to instantiate a service
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Get MP3Wrapper instance
        mp3Wrapper = MP3PlayerWrapper.getInstance();

        // Get user preferences for playback speed
        AppPreferences preferences = AppPreferences.getInstance(this);
        playbackSpeed = preferences.getPlaybackSpeed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // Retrieve notification, file path, and resume flag from the intent
            Notification notification = intent.getParcelableExtra("notification");
            String filePath = intent.getStringExtra("uri");
            resume = intent.getBooleanExtra("resume", false);

            // If not resuming, load the specified file with the playback speed
            if (!resume) {
                mp3Wrapper.load(filePath, playbackSpeed);
            }

            // Start the service in the foreground with the provided notification
            startForeground(NOTIFICATION_ID, notification);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Returns a binder for interaction with clients
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Called when the last client unbinds from the service
        // Perform cleanup or other operations here
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a new client re-binds to the service
        // Set the resume flag to true
        resume = true;
        super.onRebind(intent);
    }

    /**
     * Pauses the music playback.
     */
    public void pause() {
        if (mp3Wrapper != null) {
            mp3Wrapper.pause();
        }
    }

    /**
     * Stops the music playback and the service.
     */
    public void stop() {
        if (mp3Wrapper != null) {
            mp3Wrapper.stop();
            mp3Wrapper = null;
            stopSelf();
        }
    }

    /**
     * Resumes or starts the music playback.
     */
    public void play() {
        mp3Wrapper.play();
    }

    /**
     * Gets the duration of the currently playing music.
     *
     * @return The duration in milliseconds.
     */
    public int getDuration() {
        return mp3Wrapper.getDuration();
    }

    /**
     * Gets the current progress of the music playback.
     *
     * @return The progress in milliseconds.
     */
    public int getProgress() {
        return mp3Wrapper.getProgress();
    }
}