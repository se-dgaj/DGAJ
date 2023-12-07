package com.example.dgaj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;
import java.io.InputStream;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;

import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.text.TextUtils;
import android.widget.Toast;

public class Customer_mainScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private Button menu2, btn_set_current_location;
    private DrawerLayout drawerLayout2;
    private EditText txt_search_pos;

    private GoogleMap mMap;
    public String searchPos;
    private String searchMenu;
    private String nowAddress;
    private String[] addressParts;
    private String uid;
    private List<String> searchPosList = Arrays.asList("중구", "수성구", "달서구", "동구", "북구", "서구");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_screen);

        // 인텐트로 전달된 데이터 가져오기
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        if(!TextUtils.isEmpty(intent.getStringExtra("searchMenu"))){    //intent로 받아온 값이 NULL인지 아닌지 판별
            searchMenu = intent.getStringExtra("searchMenu");
        }else{
            searchMenu = "all"; //""=인식 못해서 all로 설정
        }
        if(!TextUtils.isEmpty(searchPos = intent.getStringExtra("searchPos"))){
            //intent로 받아온 값이 NULL인지 아닌지 판별
            if(!searchPosList.contains(searchPos)){
                searchPos = "중구";
            }
            else{
                searchPos = intent.getStringExtra("searchPos");
            }
        }else{
            searchPos = "중구";
        }

        menu2 = findViewById(R.id.menu_btn);
        this.drawerLayout2 = findViewById(R.id.customer_main_screen);
        this.btn_set_current_location = findViewById(R.id.location_change_btn);

        menu2.setOnClickListener(view -> {
            this.drawerLayout2.openDrawer(Gravity.LEFT);
        });
        txt_search_pos = findViewById(R.id.txt_search_pos);

        this.btn_set_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "현위치로 변경", Toast.LENGTH_SHORT).show();

                GetMyGeoPoint getMyGeoPoint = new GetMyGeoPoint();                                  //현재 위치 좌표 값 가져오기
                getMyGeoPoint.getLocation(Customer_mainScreen.this, new GetMyGeoPoint.LocationCallback() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        ChangeGeoPointToAddress geoPointToAddress = new ChangeGeoPointToAddress(latitude, longitude);
                        nowAddress = geoPointToAddress.getAddress(Customer_mainScreen.this);

                        //String address_gu;
                        addressParts = nowAddress.split(" ");       //"대한민국 대구광역시 수성구 교학로 3"의 경우 " "를 기준으로 끊었을 때 수성구의 값이 address_gu에 입력됨
                        if (addressParts.length > 2) {
                            String address_gu = addressParts[2];
                            if(address_gu.equals("Pkwy,")){
                                address_gu = "수성구";
                            }
                            String innersearchPos = address_gu;
                            txt_search_pos.setText(innersearchPos);

                            restaurantList.clear();
                            recyclerView = findViewById(R.id.recyclerView_main);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Customer_mainScreen.this));
                            adapter = new RestaurantAdapter(restaurantList, Customer_mainScreen.this);
                            recyclerView.setAdapter(adapter);
                            new FetchDataAsyncTask(innersearchPos).execute();
                        }

                    }
                });

            }
        });

        restaurantList.clear();
        recyclerView = findViewById(R.id.recyclerView_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(restaurantList, this);
        recyclerView.setAdapter(adapter);
        new FetchDataAsyncTask(searchPos).execute();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {                                           //구글 지도 화면
                mMap = googleMap;

                GetMyGeoPoint getMyGeoPoint = new GetMyGeoPoint();                                  //현재 위치 좌표 값 가져오기
                getMyGeoPoint.getLocation(Customer_mainScreen.this, new GetMyGeoPoint.LocationCallback() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        ChangeGeoPointToAddress geoPointToAddress = new ChangeGeoPointToAddress(latitude, longitude);
                        //String nowAddress = geoPointToAddress.getAddress(Customer_mainScreen.this);

                        LatLng myLocation = new LatLng(latitude, longitude);

                        MarkerOptions markerOptions = new MarkerOptions();                          //불러온 현재 위치를 지도에 마커로 표시
                        markerOptions.position(myLocation);

                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    }
                });
            }
        });

        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view_customer);
        navigationView2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //item.setChecked(true);
                drawerLayout2.closeDrawers();

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
                    startActivity(intent);
                }
                return false;
            }
        });
    }


    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> { // AsyncTask 클래스를 상속
        private String searchPos;  // 멤버 변수 추가

        public FetchDataAsyncTask(String searchPos) {
            this.searchPos = searchPos;
        }
        // 백그라운드 스레드에서 실행되는 작업을 정의하는 메서드
        @Override
        protected String doInBackground(Void... voids) {
            //doInBackground 메서드의 매개변수로 Void... voids를 쓰면 어떤 값이든 해당 값이 무시되고 메서드를 호출할 때 아무런 매개변수를 전달하지 않아도 된다고 합니다 그냥 void느낌?
            // 따라서 AsyncTask를 사용할 때 특정 매개변수가 필요 없는 경우에도 쓸 수 있게 넣었어요
            try {
                // 데이터를 가져올 맛집 URL
                String baseUrl = "https://www.daegufood.go.kr/kor/api/tasty.html?mode=json&addr=";
                String addSearchPos = URLEncoder.encode(searchPos, "UTF-8");
                String finalUrl = baseUrl + addSearchPos;
                URL url = new URL(finalUrl);

                // URL 연결을 설정하고 GET 요청을 보내는 HttpURLConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // 웹 페이지에서 응답 데이터를 읽어오기 위한 InputStream
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                // 데이터를 한 줄씩 읽어와서 StringBuilder에 추가
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // 읽어온 데이터를 문자열로 반환
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // 백그라운드 작업이 완료된 후에 호출되는 메서드
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // JSON 데이터를 파싱하고 UI에 표시하는 코드를 여기에 추가
                try {

                    restaurantList.clear();

                    // JSON 문자열을 파싱하여 JSONObject로 변환
                    JSONObject jsonObject = (JSONObject) new JSONTokener(result).nextValue();

                    // "data" 키의 값을 JSONArray로 가져오기
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject restaurantData = dataArray.getJSONObject(i);

                        // "FD_CS" 값을 가져오기
                        String cuisine = restaurantData.getString("FD_CS");

                        if(searchMenu.equals("all")){               //searchMenu가 공백일 때
                            // 레스토랑의 이름, 요리테마, 전화번호, 주소, 설명, 영업시간, 대표 메뉴, 가게 고유 번호, 찜처리 변수 선언
                            String name = restaurantData.getString("BZ_NM");
                            String food_type = restaurantData.getString("FD_CS");
                            String phoneNumber = restaurantData.getString("TLNO");
                            String address = restaurantData.getString("GNG_CS");
                            String description = restaurantData.optString("SMPL_DESC", "");
                            String businesshour = restaurantData.optString("MBZ_HR", "");
                            String topMenu = restaurantData.optString("MNU", "");
                            String opendata_id = restaurantData.optString("OPENDATA_ID", "");
                            int pickCount = restaurantData.optInt("PKPL", 0);

                            // Restaurant 객체를 생성하고 리스트에 값들 추가
                            Restaurant restaurant = new Restaurant(name, food_type, phoneNumber, address, description, businesshour, topMenu,  opendata_id, pickCount);
                            restaurantList.add(restaurant);
                        }

                        else if(cuisine.equals(searchMenu)) {       //searchMenu가 FD_CS일 때
                            // 레스토랑의 이름, 요리테마, 전화번호, 주소, 설명, 영업시간, 대표 메뉴, 가게 고유 번호, 찜처리 변수 선언
                            String name = restaurantData.getString("BZ_NM");
                            String phoneNumber = restaurantData.getString("TLNO");
                            String address = restaurantData.getString("GNG_CS");
                            String description = restaurantData.optString("SMPL_DESC", "");
                            String businesshour = restaurantData.optString("MBZ_HR", "");
                            String topMenu = restaurantData.optString("MNU", "");
                            String opendata_id = restaurantData.optString("OPENDATA_ID", "");
                            int pickCount = restaurantData.optInt("PKPL", 0);

                            // Restaurant 객체를 생성하고 리스트에 값들 추가
                            Restaurant restaurant = new Restaurant(name, cuisine, phoneNumber, address, description, businesshour, topMenu, opendata_id ,pickCount);
                            restaurantList.add(restaurant);
                        }
                        else {
                            String topMenu = restaurantData.optString("MNU", "");

                            if (topMenu.contains(searchMenu)) {
                                String name = restaurantData.getString("BZ_NM");
                                String phoneNumber = restaurantData.getString("TLNO");
                                String address = restaurantData.getString("GNG_CS");
                                String description = restaurantData.optString("SMPL_DESC", "");
                                String businesshour = restaurantData.optString("MBZ_HR", "");
                                String Menu = restaurantData.optString("MNU", "");
                                String opendata_id = restaurantData.optString("OPENDATA_ID", "");
                                int pickCount = restaurantData.optInt("PKPL", 0);

                                // Restaurant 객체를 생성하고 리스트에 값들 추가
                                Restaurant restaurant = new Restaurant(name, cuisine, phoneNumber, address, description, businesshour, Menu, opendata_id, pickCount);
                                restaurantList.add(restaurant);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();// adapter를 통해 데이터 변경을 노티하고 UI를 업데이트

                    adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            // 클릭 이벤트 처리 로직
                            Restaurant selectedRestaurant = restaurantList.get(position);
                            Intent intent = new Intent(getApplicationContext(), Customer_restaurantDetail.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("restaurantName", selectedRestaurant.getName());
                            intent.putExtra("restaurantPhoneNumber", selectedRestaurant.getPhoneNumber());
                            intent.putExtra("restaurantAddress", selectedRestaurant.getAddress());
                            intent.putExtra("restaurantCuisine", selectedRestaurant.getCuisine());
                            intent.putExtra("restaurantDescription", selectedRestaurant.getDescription());      // 상세 설명 필드 추가
                            intent.putExtra("restaurantBusinesshour", selectedRestaurant.getBusinesshour()); // 카테고리 필드 추가
                            intent.putExtra("restaurantTopMenu", selectedRestaurant.getTopMenu());             // 대표메뉴 필드 추가
                            intent.putExtra("restaurantOpenData_id", selectedRestaurant.getOpenData_id());  // 가게고유번호 필드 추가
                            intent.putExtra("restaurantPickCount", selectedRestaurant.getDistance());          // 찜 개수 필드 추가
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}