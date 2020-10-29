package com.hojun.blueplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hojun.blueplace.database.CloseUser;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.closeuser.GetCloseUserAsyncTask;
import com.hojun.blueplace.ui.component.CloseUserListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CloseUserActivity extends AppCompatActivity {
    private final static String TAG = "CloseUserActivity";
    private SharedViewModel sharedViewModel;

    Spinner searchSpinner, menuSpinner;
    RecyclerView closeUserRecyclerView;

    LinearLayout searchFormContainer;

    CloseUserListAdapter closeUserListAdapter;

    int menuItemIndex;
    int searchItemIndex;
    ArrayAdapter<String> menuAdapter;
    ArrayAdapter<String> searchAdapter;
    String[] menuItems;
    String[] searchItems;

    Button rightBottomButton1, rightBottomButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_user);
        // 모델 초기화
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.syncToDataBase(LocalDatabase.getInstance(this));

        // 요소 찾기
        searchFormContainer = findViewById(R.id.searchFormContainer);
        rightBottomButton1 = findViewById(R.id.rightBottomButton1);
        rightBottomButton2 = findViewById(R.id.rightBottomButton2);

        // 리스트 활성화
        closeUserRecyclerView = findViewById(R.id.closeUserRecyclerView);
        closeUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        closeUserListAdapter = new CloseUserListAdapter();
        closeUserRecyclerView.setAdapter(closeUserListAdapter);

        // 메뉴 초기화
        menuItemIndex = 0;
        searchItemIndex = 0;


        menuItems = new String[3];
        menuItems[0] = "내 지인 조회";
        menuItems[1] = "요청 승인";
        menuItems[2] = "지인 요청";

        menuSpinner = findViewById(R.id.menuSpinner);
        menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, menuItems);
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(menuAdapter);

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuItemIndex = position;
                if (menuItemIndex == 0) {
                    searchFormContainer.setVisibility(View.GONE);
                    rightBottomButton1.setText("지인 제거");

                    rightBottomButton2.setVisibility(View.GONE);

                    new GetCloseUserAsyncTask(LocalDatabase.getInstance(CloseUserActivity.this),
                            new HttpProgressInterface() {
                                @Override
                                public void onPreExecute() {
                                    closeUserListAdapter.setCloseUsers(null);
                                }

                                @Override
                                public void onPostExecute(Integer httpResult, String Message) {
                                    Log.d(TAG, String.valueOf(httpResult));
                                    Log.d(TAG, Message);
                                }

                                @Override
                                public void onProgressUpdate(Integer progress) {
                                }
                            }
                    ).execute();
                } else if (menuItemIndex == 1) {
                    searchFormContainer.setVisibility(View.GONE);
                    rightBottomButton1.setText("지인 거절");

                    rightBottomButton2.setVisibility(View.VISIBLE);
                    rightBottomButton2.setText("지인 수락");

                }
                else {
                    searchFormContainer.setVisibility(View.VISIBLE);
                    rightBottomButton1.setText("지인 요청");
                    rightBottomButton2.setVisibility(View.GONE);

                    closeUserListAdapter.setCloseUsers(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 검색 초기화
        searchItems = new String[3];
        searchItems[0] = "아이디";
        searchItems[1] = "전화 번호";
        searchItems[2] = "이메일";

        searchSpinner = findViewById(R.id.searchSpinner);
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, searchItems);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(searchAdapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchItemIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // TEST
        new GetCloseUserAsyncTask(LocalDatabase.getInstance(CloseUserActivity.this),
                new HttpProgressInterface() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(Integer httpResult, String Message) {
                        Log.d(TAG, String.valueOf(httpResult));
                        Log.d(TAG, Message);
                    }

                    @Override
                    public void onProgressUpdate(Integer progress) {
                    }
                }
        ).execute();
    }


    @Override
    protected void onResume() {
        super.onResume();

        LiveData<List<CloseUser>> closeUserLiveData = sharedViewModel.getCloseUsers();

        if (closeUserLiveData != null) {
            sharedViewModel.getCloseUsers().observe(this, new Observer<List<CloseUser>>() {
                @Override
                public void onChanged(List<CloseUser> closeUsers) {
                    if (closeUsers != null) {
                        closeUserListAdapter.setCloseUsers(closeUsers);
                    } else {
                        Log.d(TAG, "close user is null");
                    }
                }
            });
        } else {
            Log.d(TAG, "live data(close user) is null");
        }
    }
}