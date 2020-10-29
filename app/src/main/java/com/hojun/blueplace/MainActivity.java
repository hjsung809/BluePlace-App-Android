package com.hojun.blueplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.closeuser.GetCloseUserAsyncTask;

public class MainActivity extends AppCompatActivity {
    public static String serverAddr = "122.35.194.46:3000";

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 바텀 네비게이션 초기화.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        final NavController navController = navHostFragment.getNavController();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        navController.navigate(R.id.action_to_home);
                        break;
                    case R.id.navigation_map:
                        navController.navigate(R.id.action_to_map);
                        break;
                    case R.id.navigation_settings:
                        navController.navigate(R.id.action_to_settings);
                        break;
                }
                return true;
            }
        });

        // 뷰 모델 초기화.
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.syncToDataBase(LocalDatabase.getInstance(this));
    }
}