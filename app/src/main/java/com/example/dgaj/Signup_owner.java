package com.example.dgaj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signup_owner extends AppCompatActivity {

    private EditText pw_join, name_join, id_join;
    private EditText tel_join01;
    private EditText tel_join02;
    private Button btn_join, btn_id_check, btn_opendata_id_check;
    private RadioGroup choose_join_type;
    private String type;
    private EditText opendata_id_join;
    private String get_opendata_id;

    private String name; //

    TextView opendata_resname;

    private List<Map<String, Object>> mapList; // 멤버 변수로 선언

    private int index; // 멤버 변수로 선언

    private List<String> searchPosList = Arrays.asList("중구", "수성구", "달서구", "동구", "북구", "서구");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private static final String TAG = "Signup_owner";
    public Boolean checkResult = false;
    public String searchPos;

    private Spinner spinnerEmailDomains;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_owner);

        tel_join01 = findViewById(R.id.tel_join01);
        tel_join02 = findViewById(R.id.tel_join02);
        pw_join = findViewById(R.id.pw_join);
        name_join = findViewById(R.id.name_join);
        id_join = findViewById(R.id.id_join);
        btn_join = findViewById(R.id.btn_join);
        btn_id_check = findViewById(R.id.btn_id_check);
        btn_opendata_id_check = findViewById(R.id.btn_opendata_id_check);
        choose_join_type = findViewById(R.id.choose_join_type);
        opendata_id_join = findViewById(R.id.opendata_id);
        opendata_resname = findViewById(R.id.opendata_resname);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        final String[] emailcheck = new String[1];

        //스피너 어댑터 초기화
        //스피너 어댑터
        final String[] email_item = {"@gmail.com", "@naver.com", "@yu.ac.kr"};
        spinnerEmailDomains = findViewById(R.id.spinner_email_domains);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, email_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmailDomains.setAdapter(adapter);

        String[] selectedDomain = new String[1];  //String으로 선언이 안돼서 배열로 선언

        spinnerEmailDomains.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   //사용자가 선택한 스피너 목록 중 i번째 아이템을 문자열로 저장
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDomain[0] = email_item[i];
            }

            @Override   //아무것도 선택 안하면 기본 gmail로
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedDomain[0] = email_item[0];
            }
        });

        btn_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = id_join.getText().toString().trim() + selectedDomain[0];

                if (email.isEmpty()) {
                    return;
                }

                mStore.collection(FirebaseID.id_list)
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        Toast.makeText(Signup_owner.this, "이메일 중복입니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        emailcheck[0] = email;
                                        Toast.makeText(Signup_owner.this, "이메일 사용가능", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(Signup_owner.this, "오류: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btn_opendata_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                get_opendata_id = opendata_id_join.getText().toString().trim().replaceAll("[^0-9]", "");
                // opendata_id가 null이 아닌 경우에만 AsyncTask 실행
                if (get_opendata_id.length() != 0) {
                    opendata_resname.setText("식당 이름을 불러오는 중..");
                    Fetch_opendata_id();
                } else {
                    // opendata_id가 null이거나 비어있는 경우에 대한 처리 추가
                    Toast.makeText(Signup_owner.this, "가게고유번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String opendata_id = opendata_id_join.getText().toString().trim();
                final String email = id_join.getText().toString().trim() + selectedDomain[0];
                final String password = pw_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();
                final String tel = "010" + tel_join01.getText().toString().trim().replaceAll("[^0-9]", "")
                        + tel_join02.getText().toString().trim().replaceAll("[^0-9]", "");

                if ((email != null) && !email.isEmpty() && (password != null) && !password.isEmpty() && (name != null) && !name.isEmpty() && (tel != null) && !tel.isEmpty()) {
                    //Firestore안에 owner type있고, opendata_id 있는지
                    mStore.collection(FirebaseID.id_list)
                            .whereEqualTo(FirebaseID.type, "owner")
                            .whereEqualTo(FirebaseID.opendata_id, opendata_id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    //if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // 필드값 opendata_id 와 type이 "owner" 인 것 매칭
                                    boolean opendataIdMatched = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String documentOpendataId = document.getString(FirebaseID.opendata_id);
                                        if (documentOpendataId != null && documentOpendataId.equals(opendata_id)) {
                                            //Firestore안에서 opendata_id 매치되면
                                            opendataIdMatched = true;
                                            break;
                                        }
                                    }

                                    if (!opendataIdMatched) {
                                        if (email.equals(emailcheck[0])) {
                                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Signup_owner.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser user = mAuth.getCurrentUser();

                                                        Map<String, Object> userMap = new HashMap<>();
                                                        userMap.put(FirebaseID.opendata_id, opendata_id);
                                                        userMap.put(FirebaseID.email, email);
                                                        userMap.put(FirebaseID.password, password);
                                                        userMap.put(FirebaseID.name, name);
                                                        userMap.put(FirebaseID.tel, tel);
                                                        userMap.put(FirebaseID.type, type);

                                                        mStore.collection(FirebaseID.id_list).document(user.getUid()).set(userMap, SetOptions.merge());

                                                        Intent intent = new Intent(Signup_owner.this, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        String errorMessage = task.getException().getMessage();
                                                        Toast.makeText(Signup_owner.this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Signup_owner.this, "회원가입 실패:\n 아이디 중복 체크를 실행해 주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(Signup_owner.this, "회원가입 실패:\n 가게 고유번호가 이미 사용 중이거나 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                                    }
//                                    } else {
//                                        Toast.makeText(Signup_owner.this, "회원가입 실패:\n 올바른 가게 고유번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
//                                    }
                                }
                            });
                } else {
                    Toast.makeText(Signup_owner.this, "회원가입 실패:\n 빈칸 없이 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // fetchOpendataIds 수정
    private void Fetch_opendata_id() {
        checkResult = false;
        for (int i = 0; i < 6; i++) {
            searchPos = searchPosList.get(i);
            new FetchDataAsyncTask(searchPos).execute();
            if (checkResult) {
                break;
            }
        }
    }


    // FetchDataAsyncTask에서 매개변수 받도록 수정
    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {
        private String searchPosition;

        // 생성자에서 매개변수 받기
        public FetchDataAsyncTask(String searchPosition) {
            this.searchPosition = searchPosition;
        }

        // 백그라운드 스레드에서 실행되는 작업을 정의하는 메서드
        @Override
        protected String doInBackground(Void... voids) {
            //doInBackground 메서드의 매개변수로 Void... voids를 쓰면 어떤 값이든 해당 값이 무시되고 메서드를 호출할 때 아무런 매개변수를 전달하지 않아도 된다고 합니다 그냥 void느낌?
            // 따라서 AsyncTask를 사용할 때 특정 매개변수가 필요 없는 경우에도 쓸 수 있게 넣었어요
            try {
                // 데이터를 가져올 맛집 URL
                String baseUrl = "https://www.daegufood.go.kr/kor/api/tasty.html?mode=json&addr=";
                String addSearchPos = URLEncoder.encode(searchPosition, "UTF-8");
                if(true){
                    Log.e(TAG, "list = "+searchPosList);
                }
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

                        // 고유번호 값을 가져오기
                        String res_id = restaurantData.optString("OPENDATA_ID", "");

                        if (res_id.equals(get_opendata_id)) {
                            String name = restaurantData.getString("BZ_NM");

                            if(true){
                                Log.e(TAG, "rest name = "+name);
                                // 예외 처리 또는 기본값 설정
                            }

                            checkResult = true;
                            opendata_resname.setText("식당명 : "+ name);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

