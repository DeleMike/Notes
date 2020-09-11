package com.mikeinvents.notes.ui;


import android.os.Bundle;

import com.mikeinvents.notes.fragments.SettingsFragment;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //display fragment
        getSupportFragmentManager().beginTransaction().
                replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
