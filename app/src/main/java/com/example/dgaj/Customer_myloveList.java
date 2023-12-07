package com.example.dgaj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer_myloveList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private com.example.dgaj.myLoveAdapter myLoveAdapter;
    private List<myLove> myLoveList = new ArrayList<>();
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private String uid;
    private String api_opendata_id;
    public String address_gu;
    private String opendata_id;
    private List<Map<String, Object>> list; // 멤버 변수로 선언
    private int index; // 멤버 변수로 선언
    private Button btn_home;

    private void fetchOpendataIds() {
        if (index < list.size()) {
            Map<String, Object> map = list.get(index);
            if (map != null) {
                opendata_id = (String) map.get("opendata_id");
                address_gu = (String) map.get("address_gu");
                new FetchDataAsyncTask().execute();
                Toast.makeText(Customer_myloveList.this, "나의 저장목록 가져오는 중..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Customer_myloveList.this, "해당 KEY의 Map이 없습니다", Toast.LENGTH_SHORT).show();
                // Map이 null인 경우 처리
            }
        } else {
            // 모든 작업이 시작됨
            myLoveAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_mylovelist);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Customer_myloveList.this, Customer_searchScreen.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView_Love);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myLoveAdapter = new myLoveAdapter(myLoveList, (Context) Customer_myloveList.this, uid, opendata_id,address_gu);
        recyclerView.setAdapter(myLoveAdapter);

        // Firestore에서 데이터 가져오기
        DocumentReference docRef = mstore.collection(FirebaseID.cust_love_list).document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot my_love_doc = task.getResult();

                if (my_love_doc.exists()) { // 문서가 존재하는 경우에만 데이터 처리
                    list = (List) my_love_doc.getData().get("my_love_list");
                    if (list != null) {
                        // 아래 코드를 원래 있던 코드에 대체
                        if (list != null && list.size() > 0) {
                            index = 0; // index 초기화
                            fetchOpendataIds();
                        }
                        // 기존의 코드에서는 fetchOpendataIds 호출이 없었음
                    }
                    myLoveAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Customer_myloveList.this, "문서가 없습니다", Toast.LENGTH_SHORT).show();
                    // 문서가 존재하지 않을 경우 처리
                }
            } else {
                Toast.makeText(Customer_myloveList.this, "문서를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });

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
                String addSearchPos = URLEncoder.encode(address_gu, "UTF-8");
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

                        api_opendata_id = restaurantData.optString("OPENDATA_ID", "");
                        // 레스토랑의 이름, 요리테마, 전화번호, 주소, 설명, 영업시간, 대표 메뉴, 가게 고유 번호, 찜처리 변수 선언
                        String name = restaurantData.getString("BZ_NM");
                        String tel = restaurantData.getString("TLNO");
                        String time = restaurantData.getString("MBZ_HR");

                        if (opendata_id.equals(api_opendata_id)) {
                            // myWaitingres 객체를 생성하고 리스트에 값들 추가
                            myLove myLoveres = new myLove(name,tel, time, opendata_id,address_gu);
                            myLoveList.add(myLoveres);
                        }

                    }

                    myLoveAdapter.notifyDataSetChanged();// adapter를 통해 데이터 변경을 노티하고 UI를 업데이트
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            index++; // 다음 인덱스로 이동
            fetchOpendataIds(); // 다음 데이터 가져오기 호출
        }
    }
}
