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
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Class for MusicPlayer Activity.
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

    // Invokes methods when a service is being connected.
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();
            mp3Player = MP3PlayerWrapper.getInstance();
            progressBar.setMax(mp3Player.getDuration() / 1000);
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

        if (savedInstanceState != null) {
            resume = savedInstanceState.getBoolean("resume");
        }

        // Get extras from intent
        filePath = getIntent().getStringExtra("uri");
        songTitle = getIntent().getStringExtra("songTitle");

        // Load Data
        loadData();

        // Set progress bar max
        progressBar = findViewById(R.id.progressBar);
        progressBar.setEnabled(false);

        // Update Song Progress
        updateProgressPeriodically();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindFromService();
    }

    /**
     * Binds the MusicPlayer Activity to a service
     */
    private void bindToService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        Notification notification = createNotification();

        serviceIntent.putExtra("notification", notification);
        serviceIntent.putExtra("uri", filePath);
        serviceIntent.putExtra("resume", resume);

        createNotificationChannel();
        if (!resume) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent);
            }
            resume = true;
        }

        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Updates both Seekbar and TextView with the amount of time a Song has left
     */
    private void updateProgressPeriodically() {
        Handler handler = new Handler(Looper.getMainLooper());
        TextView progressText = findViewById(R.id.progressText);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp3Player = MP3PlayerWrapper.getInstance();
                String duration = formatTime(mp3Player.getDuration());
                String progress = formatTime(mp3Player.getProgress());
                updateText(duration, progress);
                updateProgressPeriodically();
            }

            private void updateText(String duration, String progress) {
                progressText.setText(progress + '/' + duration);
                progressBar.setProgress(mp3Player.getProgress()/1000);
            }
        }, 1000);
    }

    /**
     * Formats time from MP3Player, to make more readable
     *
     * @param milliseconds Time in milliseconds
     * @return The formatted time
     */
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Creates an instance of a notification with the current song title
     *
     * @return A Notification Object
     */
    private Notification createNotification() {
        return new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setContentTitle("Music Playing")
                .setContentText("Currently playing: " + songTitle)
                .setSmallIcon(R.drawable.music_note)
                .build();
    }

    /**
     * Creates a notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MP3Player";
            String description = "Playing Songs";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Unbinds the service from the activity
     */
    private void unbindFromService() {
        if (isBound) {
            unbindService(serviceConnection);
            mp3Player = null;
            isBound = false;
        }
    }

    /**
     * Plays song if bound
     */
    public void onPlayButtonPress(View v) {
        if (isBound) {
            musicService.play();
        }
    }

    /**
     * Pauses song if bound
     */
    public void onPauseButtonPress(View v) {
        if (isBound) {
            musicService.pause();
        }
    }

    /**
     * Stops the song if the service is bound, unbinds the service, and exits Activity
     */
    public void onStopButtonPress(View v) {
        if (isBound) {
            musicService.stop();
            unbindFromService();
            finish();
        }
    }

    /**
     * On back button press, returns to main activity
     *
     * @param v Current view
     */
    public void onBackButtonPress(View v) {
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * Loads data onto activity
     */
    private void loadData() {
        // Get the AppPreferences instance for managing user preferences
        AppPreferences preferences;
        preferences = AppPreferences.getInstance(this);

        // Find the main layout to set the background color based on user preferences
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());

        // Find TextView for displaying the song name
        TextView songName = findViewById(R.id.textView);

        // Find TextView for displaying the playback speed
        TextView speed = findViewById(R.id.viewSpeed);

        // Get the playback speed from preferences
        float playbackSpeed = preferences.getPlaybackSpeed();

        // Get the playback speed text from resources
        String playbackSpeedText = getResources().getString(R.string.playback);

        // Set the playback speed text with the actual speed
        speed.setText(playbackSpeedText + playbackSpeed);

        // Set the song name text to the title of the currently playing song
        songName.setText(songTitle);
    }
}
