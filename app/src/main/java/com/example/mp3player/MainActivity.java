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
*
* Main Activity for App
* */
public class MainActivity extends AppCompatActivity {

    private MP3PlayerWrapper mp3Wrapper;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private ConstraintLayout mainLayout;
    private AppPreferences preferences;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = AppPreferences.getInstance(this);
        mainLayout = findViewById(R.id.mainLayout);

        initializeList();
        loadPreferences();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult() ,
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){

                    }
                    if(result.getResultCode() == Activity.RESULT_CANCELED){}

                }
        );

    }

    //Method to initialize listView of songs
    private void initializeList(){
        final ListView lv = findViewById(R.id.listView);

        //Locates music files on device
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0", null,
                null);

        //Sets the data to listview items
        lv.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor,
                new String[] { MediaStore.Audio.Media.DATA}, new
                int[] { android.R.id.text1 }));

        //Sets on click listener to listView items
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { public
        void onItemClick(AdapterView<?> myAdapter,
                         View myView,
                         int myItemInt,
                         long mylng) {
            Cursor c = (Cursor) lv.getItemAtPosition(myItemInt);
            @SuppressLint("Range") String uri = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

            checkSongStream(uri);
        }
        });
    }

    //Method to take in a music file and change Activity startup
    private void checkSongStream(String uri) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        String songTitle = convertStringUriToTitle(uri);
        //If theres a running service start activity using onMusicContinueClick()
        if (runningServices != null) {
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (MusicService.class.getName().equals(service.service.getClassName())) {
                    mp3Wrapper = MP3PlayerWrapper.getInstance();
                    if(uri.equals(mp3Wrapper.getFilePath())) {
                        onMusicContinueClick(mp3Wrapper.getFilePath(), mp3Wrapper.getDuration(), songTitle);
                    }
                    else{
                        mp3Wrapper.stop();
                        onMusicClick(uri, songTitle);
                    }
                    return;
                }
            }
        }
        //Runs onMusicClick when no services are found
        onMusicClick(uri, songTitle);
    }

    private String convertStringUriToTitle(String uri) {
        String[] parts = uri.split("/");
        String title = parts[parts.length - 1].replace(".mp3", "");
        return title;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    //Launch MusicPlayer Activity
    public void onMusicClick(String uri, String songTitle) {
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("uri", uri);
        intent.putExtra("songTitle", songTitle);

        activityResultLauncher.launch(intent);
    }

    //Launch MusicPlayer Activity when a song is playing
    private void onMusicContinueClick(String uri, int duration, String songTitle) {
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("uri", uri);
        intent.putExtra("duration", duration);
        intent.putExtra("resume", true);
        intent.putExtra("songTitle", songTitle);

        activityResultLauncher.launch(intent);
    }

    //Launch Settings Activity
    public void onSettingsClick(View v) {
        Intent intent = new Intent(this, Settings.class);
        activityResultLauncher.launch(intent);
    }

    //Loads preferences, is set by user in Settings
    private void loadPreferences(){
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }
}