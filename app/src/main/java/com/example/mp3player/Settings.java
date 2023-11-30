package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

/**
 *
 * Class for Settings Activity
 */
public class Settings extends AppCompatActivity {
    private static final String[] speeds = {"1.0", "1.25", "1.5", "1.75", "2.0"};
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private AppPreferences preferences;
    int colour;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = AppPreferences.getInstance(this);
        mainLayout = findViewById(R.id.mainLayout);

        //Sets dropdown to speeds
        Spinner dropdown = setDropDown();

        //Sets listener to dropdown
        setDropdownListener(dropdown);

        //Sets sliders
        setSliders();
    }
    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }
    //Sets the sliders for the Activity
    private void setSliders() {
        //Gets sliders from view
        redSeekBar = findViewById(R.id.seekBarRed);
        greenSeekBar = findViewById(R.id.seekBarGreen);
        blueSeekBar = findViewById(R.id.seekBarBlue);

        //Sets listener for red seek bar
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColourView();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Sets listener for green seek bar
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColourView();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Sets listener for blue seek bar
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColourView();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //Sets dropdown to speeds
    private Spinner setDropDown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, speeds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner dropdown = findViewById(R.id.playbackSpeed);
        dropdown.setAdapter(adapter);
        for (int i = 0; i < speeds.length; i++) {
            if (Float.parseFloat(speeds[i]) == preferences.getPlaybackSpeed()) {
                dropdown.setSelection(i);
            }
        }

        return dropdown;
    }

    //Adds dropdown listener to get playback speed
    private void setDropdownListener(Spinner dropdown) {
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = speeds[position];
                preferences.setPlaybackSpeed(Float.parseFloat(selectedValue));
                MP3PlayerWrapper mp3player = MP3PlayerWrapper.getInstance();
                mp3player.setPlaybackSpeed(preferences.getPlaybackSpeed());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    //Update the colour view
    private void updateColourView(){
        View colourDisplayView = findViewById(R.id.viewColour);;
        int red = redSeekBar.getProgress();
        int green = greenSeekBar.getProgress();
        int blue = blueSeekBar.getProgress();

        colour = Color.rgb(red, green, blue);

        colourDisplayView.setBackgroundColor(colour);
    }

    //Save the colour picked
    public void saveColour(View v){
        preferences.setBackgroundColor(colour);
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }

    //Method to go back to previous Activity
    public void goBack(View v){
        finish();
    }

    //Load user data
    private void loadData(){
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }
}