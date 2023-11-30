package com.example.mp3player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *
 * Class for MusicPlayer Activity
 */
public class MusicPlayer extends AppCompatActivity {

    private static final String CHANNEL_ID = "MusicChannel";
    private String filePath;
    private MP3PlayerWrapper mp3Player;
    private MusicService musicService;
    private SeekBar progressBar;
    private boolean resume;
    private String songTitle;
    private boolean isBound = false;

    //Invokes methods when a service is being connected
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();
            mp3Player = MP3PlayerWrapper.getInstance();
            progressBar.setMax(mp3Player.getDuration()/1000);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService.stop();
            musicService = null;
            mp3Player = null;
            isBound = false;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resume", resume);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        resume = getIntent().getBooleanExtra("resume", false);

        if(savedInstanceState != null){
            Log.d("comp3018", "MusicPlayer: There is a save state");
            resume = savedInstanceState.getBoolean("resume");
        }

        //Get extras from intent
        filePath = getIntent().getStringExtra("uri");
        songTitle = getIntent().getStringExtra("songTitle");

        //Load Data
        loadData();

        //Set progress bar max
        progressBar = findViewById(R.id.progressBar);
        progressBar.setEnabled(false);

        //Update Song Progress
        updateProgressPeriodically();
    }
    @Override
    protected void onStart (){
        super.onStart();
        bindToService();
    }

    @Override
    protected void onStop (){
        super.onStop();
        unbindFromService();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindFromService();
    }

    //Binds the MusicPlayer to a service
    private void bindToService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        Notification notification = createNotification();

        serviceIntent.putExtra("notification", notification);
        serviceIntent.putExtra("uri", filePath);
        serviceIntent.putExtra("resume", resume);

        createNotificationChannel();
        Log.d("MusicPlayer", "resume TAG: " + resume);
        if(!resume){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent);
            }
            resume = true;
        }

        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    //Updates both Seekbar and TextView with amount of time a Song has left
    private void updateProgressPeriodically() {
        Handler handler = new Handler(Looper.getMainLooper());
        TextView progressText = findViewById(R.id.progressText);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp3Player = MP3PlayerWrapper.getInstance();
                String duration = String.valueOf(mp3Player.getDuration()/1000);
                String progress = String.valueOf(mp3Player.getProgress()/1000);
                updateText(duration, progress);
                updateProgressPeriodically();
            }

            private void updateText(String duration, String progress) {
                progressText.setText(progress + '/' + duration);
                progressBar.setProgress(Integer.parseInt(progress));
            }
        }, 1000);
    }

    //Returns notification for Foreground Service
    private Notification createNotification() {
        return new NotificationCompat.Builder(this ,
                CHANNEL_ID )
                .setContentTitle("Music Playing")
                .setContentText("Currently playing: " + songTitle)
                .setSmallIcon(R.drawable.music_note)
                .build();
    }

    //Create notification channel
    private void createNotificationChannel () {
        if ( Build. VERSION . SDK_INT >= Build . VERSION_CODES . O ) {
            CharSequence name = "MP3Player";
            String description = "Playing Songs";
            int importance = NotificationManager. IMPORTANCE_LOW ;
            NotificationChannel channel = new NotificationChannel ( CHANNEL_ID , name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService (NotificationManager . class ) ;
            notificationManager.createNotificationChannel ( channel ) ;
        }
    }

    //Unbinds Activity from Service
    private void unbindFromService() {
        if (isBound) {
            unbindService(serviceConnection);
            mp3Player = null;
            isBound = false;
        }
    }

    //Plays song if service is bound
    public void onPlayButtonPress(View v) {

        if(isBound) {
            musicService.play();
        }
    }

    //Pauses song if service is bound
    public void onPauseButtonPress(View v) {
        Log.d("COMP3018", "MusicPlayer: Pausing, checking bound");
        if(isBound) {
            Log.d("COMP3018", "MusicPlayer: Bound");
            Log.d("COMP3018", "MusicPlayer: Pausing");
            musicService.pause();
        }

    }

    //Stops song if service is bound, unbinds service and exits Activity
    public void onStopButtonPress(View v) {
        if(isBound) {
            musicService.stop();
            unbindFromService();
            finish();
        }
    }

    //If back button is pressed
    public void onBackButtonPress(View v){
        setResult(Activity.RESULT_OK);
        finish();
    }

    //Loads data onto Activity
    private void loadData(){
        AppPreferences preferences;
        preferences = AppPreferences.getInstance(this);

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());

        TextView songName = findViewById(R.id.textView);
        TextView speed = findViewById(R.id.viewSpeed);

        float playbackSpeed = preferences.getPlaybackSpeed();
        String playbackSpeedText = getResources().getString(R.string.playback);

        speed.setText(playbackSpeedText + playbackSpeed);

        songName.setText(songTitle);
    }
}