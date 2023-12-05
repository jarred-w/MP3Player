package com.example.mp3player;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;


/**
 *
 * Class for a MusicService Service
 */
public class MusicService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private float playbackSpeed;
    private MP3PlayerWrapper mp3Wrapper;
    private Boolean resume = false;

    //Local class to instantiate a service
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate () {
        super.onCreate();
        //Get MP3Wrapper
        mp3Wrapper = MP3PlayerWrapper.getInstance();

        //Get User Preferences
        AppPreferences preferences = AppPreferences.getInstance(this);
        playbackSpeed = preferences.getPlaybackSpeed();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public int onStartCommand ( Intent intent , int flags , int startId ) {
        if (intent != null) {
            Notification notification = intent.getParcelableExtra("notification");
            String filePath = intent.getStringExtra("uri");
            resume = intent.getBooleanExtra("resume", false);
            if(!resume){
                mp3Wrapper.load(filePath, playbackSpeed);
            }
            startForeground(NOTIFICATION_ID, notification);
        }
        return START_NOT_STICKY ;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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
        resume = true;
        super.onRebind(intent);
    }

    //Pauses music
    public void pause() {
        if (mp3Wrapper != null){
            mp3Wrapper.pause();
        }
    }
    //Stops music
    public void stop() {
        if (mp3Wrapper != null) {
            mp3Wrapper.stop();
            mp3Wrapper = null;
            stopSelf();
        }
    }

    //Plays music
    public void play() {
        mp3Wrapper.play();
    }

    //Gets duration
    public int getDuration(){
        return mp3Wrapper.getDuration();
    }

    //Gets progress
    public int getProgress(){
        return mp3Wrapper.getProgress();
    }
}