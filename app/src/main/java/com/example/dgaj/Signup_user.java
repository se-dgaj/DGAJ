package com.example.dgaj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signup_user extends AppCompatActivity {

    private EditText pw_join, name_join, id_join;
    private EditText tel_join01;
    private EditText tel_join02;
    private Button btn_join, btn_id_check;
    private RadioGroup choose_join_type;
    private String type;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Spinner spinnerEmailDomains;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup_user);
        tel_join01 = findViewById(R.id.tel_join01);
        tel_join02 = findViewById(R.id.tel_join02);
        pw_join = findViewById(R.id.pw_join);
        name_join = findViewById(R.id.name_join);
        id_join = findViewById(R.id.id_join);
        btn_join = findViewById(R.id.btn_join);
        btn_id_check = findViewById(R.id.btn_id_check);
        choose_join_type = findViewById(R.id.choose_join_type);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");


        final String[] emailcheck = new String[1];

        //스피너 어댑터 초기화
        //스피너 어댑터 --> 밑에 코드 selectedDomain[0] 해주세요~~~~~~
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

                Log.d("Signup", "Email to check: " + email);  // Debugging line

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
                                    // Check if any document has the provided email
                                    if (!task.getResult().isEmpty()) {
                                        Toast.makeText(Signup_user.this, "이메일 중복입니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        emailcheck[0]= email;
                                        Toast.makeText(Signup_user.this, "이메일 사용가능", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(Signup_user.this, "오류: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        btn_join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String email = id_join.getText().toString().trim() + selectedDomain[0];
                final String password = pw_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();
                final String tel = "010" + tel_join01.getText().toString().trim().replaceAll("[^0-9]", "") + tel_join02.getText().toString().trim().replaceAll("[^0-9]", "");

                if ((email != null) && !email.isEmpty() && (password != null) && !password.isEmpty() && (name != null) && !name.isEmpty() && (tel != null) && !tel.isEmpty()) {
                    if (email.equals(emailcheck[0])) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Signup_user.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Map<String, Object> userMap = new HashMap<>();
                                    //userMap.put(FirebaseID.documentId, user.getUid());
                                    userMap.put(FirebaseID.email, email);
                                    userMap.put(FirebaseID.password, password);
                                    userMap.put(FirebaseID.name, name);
                                    userMap.put(FirebaseID.tel, tel);
                                    userMap.put(FirebaseID.type, type);

                                    //현재 유저의 Uid를 이름으로 한 document 생성. 이게 없으면 사용자 컨텐츠의 이륾과 사용자id이름이 달라 사용하기 힘듬
                                    mStore.collection(FirebaseID.id_list).document(user.getUid()).set(userMap, SetOptions.merge());


                                    Map<String, Object> waitingMap = new HashMap<>();
                                    ArrayList<Map<String, String>> arr = new ArrayList<>();
                                    waitingMap.put("my_waiting_list", arr); //대기팀 수 확인하자
                                    mStore.collection(FirebaseID.cust_waiting_list).document(user.getUid()).set(waitingMap, SetOptions.merge());

                                    //회원가입 성공시 로그인 액티비티로 화면 전환
                                    Intent intent = new Intent(Signup_user.this, Login.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Signup_user.this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Signup_user.this, "회원가입 실패:\n 아이디 중복 체크를 실행해 주세요"  , Toast.LENGTH_SHORT).show();
                    }
                }
                else{ //빈칸 있을 때
                    Toast.makeText(Signup_user.this, "회원가입 실패:\n 빈칸 없이 모두 입력해 주세요"  , Toast.LENGTH_SHORT).show();
                }
                ;
            }
        });
    }
}