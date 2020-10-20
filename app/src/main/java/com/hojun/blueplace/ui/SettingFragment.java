package com.hojun.blueplace.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hojun.blueplace.R;
import com.hojun.blueplace.SharedViewModel;

public class SettingFragment extends Fragment {
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        View root = inflater.inflate(R.layout.fragment_setting, container, false);
//        placeCountTextView = (TextView) root.findViewById(R.id.placeCountTextView);
//        IncludedLocationTextView = (TextView)root.findViewById(R.id.IncludedLocationTextView);
//        lastVisitTimeTextView = (TextView)root.findViewById(R.id.lastVisitTimeTextView);
//        oldestVisitTimeTextView = (TextView)root.findViewById(R.id.oldestVisitTimeTextView);

//        homeViewModel.getPlaceCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                if(integer != null){
//                    placeCountTextView.setText("방문 장소 수: " + integer);
//                }
//            }
//        });

        return root;
    }
}