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
 * Settings Activity for configuring preferences.
 */
public class Settings extends AppCompatActivity {

    // Available playback speeds
    private static final String[] speeds = {"1.0", "1.25", "1.5", "1.75", "2.0"};

    // SeekBars for adjusting RGB values
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;

    // Preferences for storing user settings
    private AppPreferences preferences;

    // Combined color value
    private int colour;

    // Main layout of the activity
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize preferences and main layout
        preferences = AppPreferences.getInstance(this);
        mainLayout = findViewById(R.id.mainLayout);

        // Sets dropdown to speeds
        Spinner dropdown = setDropDown();

        // Sets listener to dropdown
        setDropdownListener(dropdown);

        // Sets sliders
        setSliders();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load data when the activity resumes
        loadData();
    }

    /**
     * Sets the sliders for adjusting RGB values.
     */
    private void setSliders() {
        // Gets sliders from view
        redSeekBar = findViewById(R.id.seekBarRed);
        greenSeekBar = findViewById(R.id.seekBarGreen);
        blueSeekBar = findViewById(R.id.seekBarBlue);

        // Sets listener for red seek bar
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

        // Sets listener for green seek bar
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

        // Sets listener for blue seek bar
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

    /**
     * Sets the dropdown for selecting playback speed.
     *
     * @return The initialized Spinner object.
     */
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

    /**
     * Adds a dropdown listener to get the selected playback speed.
     *
     * @param dropdown The Spinner object to which the listener is added.
     */
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

    /**
     * Updates the color view based on the current RGB values.
     */
    private void updateColourView() {
        View colourDisplayView = findViewById(R.id.viewColour);
        int red = redSeekBar.getProgress();
        int green = greenSeekBar.getProgress();
        int blue = blueSeekBar.getProgress();

        colour = Color.rgb(red, green, blue);

        colourDisplayView.setBackgroundColor(colour);
    }

    /**
     * Saves the selected color and updates the main layout background.
     *
     * @param v The View object representing the clicked button.
     */
    public void saveColour(View v) {
        preferences.setBackgroundColor(colour);
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }

    /**
     * Navigates back to the previous Activity.
     *
     * @param v The View object representing the clicked button.
     */
    public void goBack(View v) {
        finish();
    }

    /**
     * Loads user preferences and updates the main layout background.
     */
    private void loadData() {
        mainLayout.setBackgroundColor(preferences.getBackgroundColour());
    }
}