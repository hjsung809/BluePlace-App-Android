package com.hojun.blueplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hojun.blueplace.R;
import com.hojun.blueplace.ui.component.CliqueListAdapter;
import com.hojun.blueplace.ui.component.CloseUserListAdapter;

public class CliqueActivity extends AppCompatActivity {
    private final static String TAG = "CloseUserActivity";
    Spinner searchSpinner, menuSpinner;
    RecyclerView cliqueRecyclerView;

    LinearLayout searchFormContainer;

    CliqueListAdapter cliqueListAdapter;

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
        setContentView(R.layout.activity_clique);

        searchFormContainer = findViewById(R.id.searchFormContainer);
        rightBottomButton1 = findViewById(R.id.rightBottomButton1);
        rightBottomButton2 = findViewById(R.id.rightBottomButton2);

        // 리스트 활성화
        cliqueRecyclerView = findViewById(R.id.cliqueRecyclerView);
        cliqueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cliqueListAdapter = new CliqueListAdapter();
        cliqueRecyclerView.setAdapter(cliqueListAdapter);

        // 메뉴 초기화
        menuItemIndex = 0;
        searchItemIndex = 0;
        menuItems = new String[2];
        menuItems[0] = "가입 현황";
        menuItems[1] = "검색 및 가입";

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
                    rightBottomButton1.setText("그룹 탈퇴");
                    rightBottomButton2.setVisibility(View.VISIBLE);
                    rightBottomButton2.setText("그룹 관리");                } else {
                    searchFormContainer.setVisibility(View.VISIBLE);
                    rightBottomButton1.setText("가입 신청");
                    rightBottomButton2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 검색 초기화
        searchItems = new String[2];
        searchItems[0] = "아이디";
        searchItems[1] = "그룹 이름";
//        searchItems[2] = "그룹장 아이디";

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
    }
}