package com.hojun.blueplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hojun.blueplace.database.CloseUser;
import com.hojun.blueplace.ui.component.CloseUserListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CloseUserActivity extends AppCompatActivity {
    Spinner searchSpinner, menuSpinner;
    RecyclerView closeUserRecyclerView;

    LinearLayout searchFormContainer;

    CloseUserListAdapter closeUserListAdapter;

    int menuItemIndex;
    ArrayAdapter<String> menuAdapter;
    ArrayAdapter<String> searchAdapter;
    String[] menuItems;
    String[] searchItems;

    Button rightBottomButton1, rightBottomButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_user);

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
        menuItems = new String[2];
        menuItems[0] = "조회 및 관리";
        menuItems[1] = "검색 및 추가";

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

                    rightBottomButton2.setVisibility(View.VISIBLE);
                    rightBottomButton2.setText("지인 수락");
                } else {
                    searchFormContainer.setVisibility(View.VISIBLE);
                    rightBottomButton1.setText("지인 요청");
                    rightBottomButton2.setVisibility(View.GONE);
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



        List<CloseUser> closeUsers = new ArrayList<>();
        CloseUser closeUser;

        closeUser = new CloseUser();
        closeUser.id = 1;
        closeUser.email = "123@naver.com";
        closeUser.phoneNumber = "010-xxxx-xxxx";
        closeUsers.add(closeUser);

        closeUser = new CloseUser();
        closeUser.id = 2;
        closeUser.email = "456@naver.com";
        closeUser.phoneNumber = "010-xxxx-xxxx";
        closeUsers.add(closeUser);

        closeUser = new CloseUser();
        closeUser.id = 3;
        closeUser.email = "78@naver.com";
        closeUser.phoneNumber = "010-xxxx-xxxx";
        closeUsers.add(closeUser);

        closeUser = new CloseUser();
        closeUser.id = 4;
        closeUser.email = "90@naver.com";
        closeUser.phoneNumber = "010-xxxx-xxxx";
        closeUsers.add(closeUser);

        closeUserListAdapter.setCloseUsers(closeUsers);
    }
}