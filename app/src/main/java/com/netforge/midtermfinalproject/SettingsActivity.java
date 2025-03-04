package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioButton lightThemeRadio, darkThemeRadio;
    private Button updateProfileButton, clearCacheButton, contactSupportButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        lightThemeRadio = findViewById(R.id.lightThemeRadio);
        darkThemeRadio = findViewById(R.id.darkThemeRadio);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        clearCacheButton = findViewById(R.id.clearCacheButton);
        contactSupportButton = findViewById(R.id.contactSupportButton);

        // Load saved theme preference
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int savedTheme = sharedPreferences.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeRadio.setChecked(true);
        } else {
            lightThemeRadio.setChecked(true);
        }

        // Set up theme selection
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lightThemeRadio) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.darkThemeRadio) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        /*
        // Set up profile update button
        updateProfileButton.setOnClickListener(v -> {
            // Navigate to profile update screen
            Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });*/

        // Set up clear cache button
        clearCacheButton.setOnClickListener(v -> {
            clearApplicationCache();
            Toast.makeText(this, "Cached data cleared successfully", Toast.LENGTH_SHORT).show();
        });

        // Set up contact support button
        contactSupportButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822"); // Ensures only email apps handle it
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"anguzuroggers0@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - VeriStockX");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Support Team,\n\nPlease assist me with the following issue:\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Choose an Email app"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(v.getContext(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Save the selected theme preference
    private void saveThemePreference(int themeMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("app_theme", themeMode);
        editor.apply();
    }

    // Clear application cache
    private void clearApplicationCache() {
        try {
            File cacheDir = getCacheDir();
            deleteDirectory(cacheDir);

            File externalCacheDir = getExternalCacheDir();
            if (externalCacheDir != null) {
                deleteDirectory(externalCacheDir);
            }

            Log.d("SettingsActivity", "Cache cleared successfully");
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error clearing cache: " + e.getMessage());
        }
    }

    // Recursive method to delete directories and files
    private boolean deleteDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
        return directory.delete();
    }
}