package com.example.mp3player;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

/**
 * Main Activity for the MP3 Player App.
 */
public class MainActivity extends AppCompatActivity {

    // ActivityResultLauncher for handling activity results.
    ActivityResultLauncher<Intent> activityResultLauncher;

    // Reference to the main layout of the activity.
    private ConstraintLayout mainLayout;

    // Instance of AppPreferences for managing user preferences.
    private AppPreferences preferences;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AppPreferences and mainLayout.
        preferences = AppPreferences.getInstance(this);
        mainLayout = findViewById(R.id.mainLayout);

        // Initialize the list of songs and load preferences.
        initializeList();
        loadPreferences();

        // Register activity result launcher for handling activity results.
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Handle result when the activity returns OK.
                    }
                    if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // Handle result when the activity is canceled.
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reload preferences on resume.
        loadPreferences();
    }

    /**
     * Method to initialize the ListView of songs.
     */
    private void initializeList() {
        final ListView lv = findViewById(R.id.listView);

        // Locate music files on the device.
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0", null,
                null);

        // Set the data to ListView items using SimpleCursorAdapter.
        lv.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{MediaStore.Audio.Media.DATA}, new
                int[]{android.R.id.text1}));

        // Set on click listener to ListView items.
        lv.setOnItemClickListener((AdapterView<?> myAdapter,
                                   View myView,
                                   int myItemInt,
                                   long mylng) -> {
            Cursor c = (Cursor) lv.getItemAtPosition(myItemInt);
            @SuppressLint("Range") String uri = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

            checkSongStream(uri);
        });
    }

    /**
     * Method to handle a music file and change activity startup.
     *
     * @param uri The URI of the music file.
     */
    private void checkSongStream(String uri) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        String songTitle = convertStringUriToTitle(uri);

        // If there's a running service, start the activity using onMusicContinueClick().
        if (runningServices != null) {
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (MusicService.class.getName().equals(service.service.getClassName())) {
                    MP3PlayerWrapper mp3Wrapper = MP3PlayerWrapper.getInstance();
                    if (uri.equals(mp3Wrapper.getFilePath())) {
                        onMusicContinueClick(mp3Wrapper.getFilePath(), mp3Wrapper.getDuration(), songTitle);
                    } else {
                        mp3Wrapper.stop();
                        onMusicClick(uri, songTitle);
                    }
                    return;
                }
            }
        }
        // Run onMusicClick when no services are found.
        onMusicClick(uri, songTitle);
    }

    /**
     * Convert a string URI to a title by extracting the file name.
     *
     * @param uri The URI of the file.
     * @return The title extracted from the URI.
     */
    private String convertStringUriToTitle(String uri) {
        String[] parts = uri.split("/");
        return parts[parts.length - 1].replace(".mp3", "");
    }

    /**
     * Launch MusicPlayer Activity.
     *
     * @param uri       The URI of the music file.
     * @param songTitle The title of the song.
     */
    public void onMusicClick(String uri, String songTitle) {
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("uri", uri);
        intent.putExtra("songTitle", songTitle);

        activityResultLauncher.launch(intent);
    }

    /**
     * Launch MusicPlayer Activity when a song is playing.
     *
     * @param uri       The URI of the music file.
     * @param duration  The duration of the song.
     * @param songTitle The title of the song.
     */
    private void onMusicContinueClick(String uri, int duration, String songTitle) {
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("uri", uri);
        intent.putExtra("duration", duration);
        intent.putExtra("resume", true);
        intent.putExtra("songTitle", songTitle);

        activityResultLauncher.launch(intent);
    }

    /**
     * Launch Settings Activity.
     *
     * @param v The View that triggered the method.
     */
    public void onSettingsClick(View v) {
        Intent intent = new Intent(this, Settings.class);
        activityResultLauncher.launch(intent);
    }

    /**
     * Load preferences and set the background color of the main layout.
     */
    private void loadPreferences() {
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }
}