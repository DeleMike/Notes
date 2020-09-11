package com.mikeinvents.notes.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;


import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


import com.mikeinvents.notes.R;

import java.util.Objects;


public class SettingsFragment extends PreferenceFragmentCompat {
   // public static final String THEME_PREF_SWITCH = "dark_mode";
    private static final String FEEDBACK = "feedback";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        //Preference darkMode = findPreference(THEME_PREF_SWITCH);
        Preference feedback = findPreference(FEEDBACK);

        //change theme
//        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                boolean darkMode = (Boolean) o;
//                if(darkMode){
//                    //set the night theme
//                  // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                   //restartApp();
//                    Toast.makeText(getContext(), "Dark Mode Coming soon", Toast.LENGTH_SHORT).show();
//                }else{
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    Toast.makeText(getContext(), "Light Mode in use", Toast.LENGTH_SHORT).show();
//                    restartApp();
//                }
//                //Objects.requireNonNull(getActivity()).recreate();
//
//                return true;
//            }
//        });

        //send feedback through mails
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"akindelemichael65@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Trivia-On-Go Feedback");
                intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.settings_send_feedback_summary));

                //verify if the intent will resolve to at least one activity
                PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
                if(intent.resolveActivity(packageManager) != null){
                    startActivity(Intent.createChooser(intent,"Send feedback"));
                }
                return true;
            }
        });

    }


}
