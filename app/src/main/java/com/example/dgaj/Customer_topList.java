package com.example.dgaj;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Customer_topList extends AppCompatActivity {


    private DrawerLayout drawerLayout;
    private RestaurantAdapter adapter;

    private List<Restaurant> restaurantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<String> searchPosList = Arrays.asList("중구", "수성구", "달서구", "동구", "북구", "서구");
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private static final String TAG = "Customer_topList";
    private String uid;
    private String RES_ID;
    private String searchPos;
    private String nowAddress;
    private int index; // 멤버 변수로 선언
    private ArrayList<String> rest_idList = new ArrayList<>();
    private ArrayList<Long> love_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_top_list);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        searchPos = intent.getStringExtra("searchPos");
        nowAddress = intent.getStringExtra("nowAddress");

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

        // 데이터를 추가하기 전에 arrayList를 초기화
        restaurantList.clear();

        drawerLayout = findViewById(R.id.customer_top_list);
        adapter = new RestaurantAdapter(restaurantList, Customer_topList.this);
        recyclerView = findViewById(R.id.recyclerView_top);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        mStore.collection(FirebaseID.rest_love_list)
                .whereEqualTo("address_gu", searchPos)
                .orderBy("totallove", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot doc = task.getResult();
                            if (!doc.isEmpty()) {
                                List<DocumentSnapshot> query_result = doc.getDocuments();
                                if (!query_result.isEmpty()) {
                                    rest_idList.clear();
                                    for (int i = 0; i < query_result.size(); i++) {
                                        String id = (String) query_result.get(i).getData().get("opendata_id");
                                        Long love = (Long) query_result.get(i).getData().get("totallove");
                                        love_list.add(love);
                                        rest_idList.add(id);
                                    }
                                    index = 0; // index 초기화
                                    fetchOpendataIds();
                                    Toast.makeText(Customer_topList.this, "탑 리스트 가져오는 중..", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Customer_topList.this, "문서가 없습니다", Toast.LENGTH_SHORT).show();
                                    // 문서가 존재하지 않을 경우 처리
                                }
                            } else {
                                // Firestore에서 문서를 가져오는 데 실패한 경우 처리
                                Toast.makeText(Customer_topList.this, "문서를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 업데이트 실패 처리
                        Toast.makeText(Customer_topList.this, "위치검색어 문서 실패", Toast.LENGTH_SHORT).show();
                    }
                });


        // RecyclerView 항목 클릭 이벤트 리스너 설정 부분
        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {         //가게 정보 다음 페이지에 보내기
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


    }

    void fetchOpendataIds() {

        if (index < rest_idList.size()) {
            RES_ID = rest_idList.get(index);
            new Customer_topList.FetchDataAsyncTask().execute();
        } else {
            // 모든 작업이 시작됨
            adapter.notifyDataSetChanged();
        }
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> { // AsyncTask 클래스를 상속
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
                    // JSON 문자열을 파싱하여 JSONObject로 변환
                    JSONObject jsonObject = (JSONObject) new JSONTokener(result).nextValue();

                    // "data" 키의 값을 JSONArray로 가져오기
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    // 결과를 저장하기위한 StringBuilder를 초기화
                    StringBuilder resultBuilder = new StringBuilder();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject restaurantData = dataArray.getJSONObject(i);

                        // "FD_CS" 값을 가져오기
                        String top_RES_ID = restaurantData.optString("OPENDATA_ID", "");
                        if (true) {
                            Log.e(TAG, "api RES_ID 확인" + top_RES_ID + " RES_ID : " + RES_ID);
                            // 예외 처리 또는 기본값 설정
                            //return;
                        }
                        if (top_RES_ID.equals(RES_ID)) {
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
                            Restaurant restaurant = new Restaurant(name, food_type, phoneNumber, address, description, businesshour, topMenu, opendata_id, pickCount);
                            restaurantList.add(restaurant);
                            if (true) {
                                Log.e(TAG, "top List 완" + restaurantList.get(index).getName());
                                Log.e(TAG, "top List 완" + restaurantList.get(index).getOpenData_id());
                                // 예외 처리 또는 기본값 설정
                                //return;
                            }
                            break;
                        }
                    }

                    adapter.notifyDataSetChanged();// adapter를 통해 데이터 변경을 노티하고 UI를 업데이트
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            index++; // 다음 인덱스로 이동
            fetchOpendataIds(); // 다음 데이터 가져오기 호출
        }
    }
}
