package com.pontonsoft.keepincontact.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.pontonsoft.keepincontact.R;

public class UserSettingActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}