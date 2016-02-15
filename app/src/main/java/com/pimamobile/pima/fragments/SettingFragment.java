package com.pimamobile.pima.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.pimamobile.pima.R;

public class SettingFragment extends PreferenceFragmentCompat{

    private SharedPreferences mPreferences;

    public static SettingFragment newInstance(){
        return new SettingFragment();
    }
    public SettingFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
