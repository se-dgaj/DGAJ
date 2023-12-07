package com.example.dgaj;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;
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
import java.util.List;
import java.util.Map;

public class Customer_myWaiting extends AppCompatActivity {

    private String uid;
    private String RES_ID;
    public String waiting_addr_gu;

    private DrawerLayout drawerLayout;
    private myWaitingAdapter adapter;
    private ArrayList<myWaiting> arrayList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();

    private RecyclerView recyclerView;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private static final String TAG = "Customer_myWaiting";

    private List<Map<String, Object>> mapList; // 멤버 변수로 선언

    private int index; // 멤버 변수로 선언

    private Button btn_home;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_my_waiting);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        // 데이터를 추가하기 전에 arrayList를 초기화
        arrayList.clear();
        restaurantList.clear();

        drawerLayout = findViewById(R.id.customer_my_waiting);
        adapter = new myWaitingAdapter(arrayList, restaurantList, this, uid);
        recyclerView = findViewById(R.id.recyclerView_myWaiting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Customer_myWaiting.this, Customer_searchScreen.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });



        // Firestore에서 데이터 가져오기
        DocumentReference docRef = mstore.collection(FirebaseID.cust_waiting_list).document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot wait_doc = task.getResult();

                if (wait_doc.exists()) { // 문서가 존재하는 경우에만 데이터 처리
                    mapList = (List) wait_doc.getData().get("my_waiting_list");
                    // 아래 코드를 원래 있던 코드에 대체
                    if (mapList != null && mapList.size() > 0) {
                        index = 0; // index 초기화
                        fetchOpendataIds();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Customer_myWaiting.this, "문서가 없습니다", Toast.LENGTH_SHORT).show();
                    // 문서가 존재하지 않을 경우 처리
                }
            } else {
                // Firestore에서 문서를 가져오는 데 실패한 경우 처리
                Toast.makeText(Customer_myWaiting.this, "문서를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void fetchOpendataIds() {
        if (index < mapList.size()) {
            Map<String, Object> map = mapList.get(index);
            if (map != null) {
                RES_ID = (String) map.get("opendata_id");
                waiting_addr_gu = (String) map.get("restaurant_addr_gu");
                new FetchDataAsyncTask().execute();
                Toast.makeText(Customer_myWaiting.this, "가게 ID 가져오기 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Customer_myWaiting.this, "해당 KEY의 Map이 없습니다", Toast.LENGTH_SHORT).show();
                // Map이 null인 경우 처리
            }
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
                String addSearchPos = URLEncoder.encode(waiting_addr_gu, "UTF-8");
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
                        String waiting_RES_ID = restaurantData.optString("OPENDATA_ID", "");
                        if(true){
                            Log.e(TAG, "api RES_ID 확인"+waiting_RES_ID+" RES_ID : "+RES_ID);
                            // 예외 처리 또는 기본값 설정
                            //return;
                        }
                        if (waiting_RES_ID.equals(RES_ID)) {
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
                            if(true){
                                Log.e(TAG, "rest List 완"+restaurantList.size());
                                // 예외 처리 또는 기본값 설정
                                //return;
                            }

                            myWaiting my_waiting = new myWaiting(name, address, food_type, RES_ID);
                            arrayList.add(my_waiting);
                            if(true){
                                Log.e(TAG, "waitList 완"+arrayList.size()+name);
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
