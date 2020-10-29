package com.hojun.blueplace.ui.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.hojun.blueplace.R;
import com.hojun.blueplace.SharedViewModel;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG= "SettingsFragment";
    private SharedViewModel sharedViewModel;
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    EditTextPreference userEmailPreference;
    EditTextPreference userPhoneNumberPreference;
    EditTextPreference userNamePreference;

    SwitchPreferenceCompat locationRecordPreference;
    SwitchPreferenceCompat accurateLocationPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        localDatabase = LocalDatabase.getInstance(getContext());
        userDataDao = localDatabase.userDataDao();

        locationRecordPreference = findPreference("location-record");
        accurateLocationPreference = findPreference("accurate-location");

        userEmailPreference = findPreference("user-email");
        userPhoneNumberPreference = findPreference("user-phone-number");
        userNamePreference = findPreference("user-name");


        // DB에 있는 데이터와 설정의 동기화.
        sharedViewModel.getUserData().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(List<UserData> userData) {
                for(UserData ud: userData) {
                    // Log.d(TAG, ud.id + "," + ud.value);
                    switch (ud.id) {
                        case "location-record":
                            locationRecordPreference.setChecked(ud.value.equals("true"));
                            break;
                        case "accurate-location":
                            accurateLocationPreference.setChecked(ud.value.equals("true"));
                            break;
                    }
                }
            }
        });

        // 설정 변경시, DB에 반영.
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // Log.d(TAG, "key: " + key + " , value: " + sharedPreferences.getBoolean(key, false));
                switch (key) {
                    case "location-record":
                    case "accurate-location":
                        String active = "" + sharedPreferences.getBoolean(key, false);
                        reflectUserDataToDB(key, active);
                        break;
                }
            }
        };
    }

    // DB에 쓰기.
    private void reflectUserDataToDB(final String id, final String value) {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    UserData userData = userDataDao.getValue(id);

                    if (userData == null) {
                        userData = new UserData();
                        userData.id = id;
                        userData.value = value;
                        userDataDao.insert(userData);
                    } else {
                        userData.id = id;
                        userData.value = value;
                        userDataDao.update(userData);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                return null;
            }
        };
        asyncTask.execute();
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

}