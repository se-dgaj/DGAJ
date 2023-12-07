package com.example.dgaj;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;
import java.util.TimeZone;

public class Customer_searchScreen extends AppCompatActivity {

    private Button menu, btn_search, btn_game, btn_set_current_location,btn_random_select, btn_top10;
    private DrawerLayout drawerLayout;
    private EditText txt_search_pos, txt_search_menu;
    public String searchPos;
    public String uid;
    public String searchMenu;
    String[] menuList;
    long mNow;
    Date mdate;
    TimeZone tz;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    TextView date_search_screen;
    String nowAddress="";
    String[] addressParts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search_screen);
        date_search_screen=(TextView) findViewById(R.id.date_search_screen);

        // JSON 파일에서 메뉴 리스트 읽어오기

        try {
            // JSON 파일을 읽어옴
            InputStream inputStream = getAssets().open("menuList.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            // JSON 배열로 변환
            JSONArray jsonArray = new JSONArray(json);

            // 배열 크기에 맞춰 메뉴 리스트 배열 생성
            menuList = new String[jsonArray.length()];

            // 배열에 메뉴 항목 추가
            for (int i = 0; i < jsonArray.length(); i++) {
                menuList[i] = jsonArray.getString(i);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // JSON 파일 읽기 실패 시 기본 메뉴 리스트 사용
            //menuList = new String[]{"순대국밥", "돼지국밥", "섞어국밥", "수육", "순대한접시", "곰탕", "양곰탕", "냉면", "갈비탕", "막국수", "소갈비찜"};
        }


        txt_search_pos = findViewById(R.id.txt_search_pos);
        txt_search_menu = findViewById(R.id.txt_search_menu);
        btn_random_select = findViewById(R.id.btn_random_select);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        btn_game = findViewById(R.id.btn_game);
        btn_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_searchScreen.this, Customer_game_choose.class);
                startActivity(intent);
            }
        });

        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "검색 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                searchPos = txt_search_pos.getText().toString();
                searchMenu = txt_search_menu.getText().toString();

                if(TextUtils.isEmpty(searchPos)){
                    GetMyGeoPoint getMyGeoPoint1 = new GetMyGeoPoint(); //현재 위치 좌표 값 가져오기
                    getMyGeoPoint1.getLocation(Customer_searchScreen.this, new GetMyGeoPoint.LocationCallback() {
                        @Override
                        public void onLocationReceived(double latitude, double longitude) {
                            ChangeGeoPointToAddress geoPointToAddress = new ChangeGeoPointToAddress(latitude, longitude);
                            nowAddress = geoPointToAddress.getAddress(Customer_searchScreen.this);

                            addressParts = nowAddress.split(" "); //"대한민국 대구광역시 수성구 교학로 3"의 경우 " "를 기준으로 끊었을 때 수성구의 값이 address_gu에 입력됨
                            if (addressParts.length > 2) {
                                String address_gu = addressParts[2];
                                searchPos = address_gu;
                            }
                        }
                    });
                }
                Intent intent = new Intent(Customer_searchScreen.this, Customer_mainScreen.class);
                intent.putExtra("searchPos", searchPos);
                intent.putExtra("searchMenu", searchMenu);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        this.menu = findViewById(R.id.menu_btn);
        this.drawerLayout = findViewById(R.id.customer_search_screen);
        this.btn_set_current_location = findViewById(R.id.location_change_btn);

        this.menu.setOnClickListener(view -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_customer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //item.setChecked(true);
                drawerLayout.closeDrawers();

                int id = item.getItemId();

                if (id == R.id.my_picked_list){
                    //Toast.makeText(getApplicationContext(), "나의 저장 목록 클릭됨", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), Customer_myloveList.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                } else if (id == R.id.waiting_state) {
                    //Toast.makeText(getApplicationContext(), "대기 현황 클릭됨", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), Customer_myWaiting.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }else if (id == R.id.my_account) {
                    //Toast.makeText(getApplicationContext(), "내 계정 클릭됨", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), Customer_myAccount.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                } else if (id == R.id.logout) {
                    //Toast.makeText(getApplicationContext(), "로그아웃 클릭됨", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), Login.class);
                    intent.putExtra("uid", uid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });

        this.btn_set_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetMyGeoPoint getMyGeoPoint = new GetMyGeoPoint(); //현재 위치 좌표 값 가져오기
                getMyGeoPoint.getLocation(Customer_searchScreen.this, new GetMyGeoPoint.LocationCallback() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        ChangeGeoPointToAddress geoPointToAddress = new ChangeGeoPointToAddress(latitude, longitude);
                        nowAddress = geoPointToAddress.getAddress(Customer_searchScreen.this);

                        //String address_gu;
                        addressParts = nowAddress.split(" "); //"대한민국 대구광역시 수성구 교학로 3"의 경우 " "를 기준으로 끊었을 때 수성구의 값이 address_gu에 입력됨
                        if (addressParts.length > 2) {
                            String address_gu = addressParts[2];
                            searchPos = address_gu;
                        }
                        Toast.makeText(getApplicationContext(), "현위치로 변경", Toast.LENGTH_SHORT).show();
                        txt_search_pos.setText(searchPos);
                    }
                });
            }
        });
        btn_top10 = findViewById(R.id.btn_top10);
        btn_top10.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String pos = txt_search_pos.getText().toString();
                Intent intent = new Intent(Customer_searchScreen.this, Customer_topList.class);

                if(pos.isEmpty()){
                    GetMyGeoPoint getMyGeoPoint = new GetMyGeoPoint(); //현재 위치 좌표 값 가져오기
                    getMyGeoPoint.getLocation(Customer_searchScreen.this, new GetMyGeoPoint.LocationCallback() {
                        @Override
                        public void onLocationReceived(double latitude, double longitude) {
                            ChangeGeoPointToAddress geoPointToAddress = new ChangeGeoPointToAddress(latitude, longitude);
                            nowAddress = geoPointToAddress.getAddress(Customer_searchScreen.this);

                            //String address_gu;
                            addressParts = nowAddress.split(" "); //"대한민국 대구광역시 수성구 교학로 3"의 경우 " "를 기준으로 끊었을 때 수성구의 값이 address_gu에 입력됨
                            if (addressParts.length > 2) {
                                String address_gu = addressParts[2];
                                searchPos = address_gu;

                                intent.putExtra("searchPos", searchPos);
                                intent.putExtra("uid", uid);
                                intent.putExtra("nowAddress", nowAddress);
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    searchPos = pos;
                    intent.putExtra("searchPos", searchPos);
                    intent.putExtra("uid", uid);
                    intent.putExtra("nowAddress", nowAddress);

                    startActivity(intent);
                }
            }
        });

        btn_random_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 랜덤으로 메뉴 선택
                Random random = new Random();
                int index = random.nextInt(menuList.length);
                String randomMenu = menuList[index];

                // 선택된 메뉴를 텍스트로 설정
                txt_search_menu.setText(randomMenu);
            }
        });
        tz=TimeZone.getTimeZone("Asia/Seoul");
        date_search_screen.setText(getTime());

    }
    private String getTime(){
        mFormat.setTimeZone(tz);
        mNow=System.currentTimeMillis();
        mdate=new Date(mNow);
        return mFormat.format(mdate);
    }

}