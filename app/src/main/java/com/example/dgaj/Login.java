package com.example.dgaj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText id_login, pw_login;
    private Button btn_login, btn_signup,btn_find_id_pw;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String RES_ID; // 상수로 정의
    private CustomAnimationDialog customAnimationDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id_login = findViewById(R.id.id_login);
        pw_login = findViewById(R.id.pw_login);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        btn_find_id_pw = findViewById(R.id.btn_find_id_pw);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = id_login.getText().toString().trim();
                final String password = pw_login.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    //로딩화면 시작
                    customAnimationDialog = new CustomAnimationDialog(Login.this);
                    customAnimationDialog.show();

                    // Firebase Authentication을 사용하여 이메일과 비밀번호로 사용자 인증
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 로그인에 성공한 경우 UID를 가져오기
                                        String uid = mAuth.getCurrentUser().getUid();

                                        // Firestore에서 해당 UID의 문서를 가져오기
                                        mStore.collection(FirebaseID.id_list).document(uid).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                String userType = document.getString("type");
                                                                RES_ID = document.getString("opendata_id");

                                                                if ("user".equals(userType)) {
                                                                    // 사용자 역할이 'user'인 경우
                                                                    Intent intent = new Intent(Login.this, Customer_searchScreen.class);
                                                                    intent.putExtra("uid", uid);
                                                                    startActivity(intent);
                                                                    customAnimationDialog.dismiss(); //로딩화면 종료
                                                                    finish();
                                                                } else if ("owner".equals(userType)) {
                                                                    // 사용자 역할이 'owner'인 경우
                                                                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                                                    DocumentReference docIdRef = rootRef.collection(FirebaseID.rest_waiting_list).document(RES_ID);
                                                                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot document = task.getResult();
                                                                                if (document.exists()) {
                                                                                    Intent intent = new Intent(Login.this, Owner_mainScreen.class);
                                                                                    intent.putExtra("uid", uid);
                                                                                    intent.putExtra("RES_ID", RES_ID);
                                                                                    startActivity(intent);
                                                                                    customAnimationDialog.dismiss(); //로딩화면 종료
                                                                                    finish();
                                                                                } else {
                                                                                    Intent intent = new Intent(Login.this, Owner_mainScreen_unopened.class);
                                                                                    intent.putExtra("uid", uid);
                                                                                    intent.putExtra("RES_ID", RES_ID);
                                                                                    startActivity(intent);
                                                                                    customAnimationDialog.dismiss(); //로딩화면 종료
                                                                                    finish();
                                                                                }
                                                                            } else {
                                                                                customAnimationDialog.dismiss(); //로딩화면 종료
                                                                                Toast.makeText(Login.this, "대기창 유무 확인 실패", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    customAnimationDialog.dismiss(); //로딩화면 종료
                                                                    Toast.makeText(Login.this, "로그인 실패...! \n로그인 버튼 타입을 확인하세요", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                customAnimationDialog.dismiss(); //로딩화면 종료
                                                                Toast.makeText(Login.this, "로그인 실패...! \n사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            customAnimationDialog.dismiss(); //로딩화면 종료
                                                            Toast.makeText(Login.this, "로그인 실패...! \nFirestore에서 사용자 정보를 가져오는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });
                                    } else {
                                        //Toast.makeText(Login.this, "로그인 실패...! \n이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();

                                        customAnimationDialog.dismiss(); //로딩화면 종료
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode(); //오류 코드 가져오기

                                        if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                                            Toast.makeText(Login.this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                        } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                            Toast.makeText(Login.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Login.this, "로그인 실패...! \n" + "사용자를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Choose_SignupType.class);
                startActivity(intent);
            }
        });

        btn_find_id_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Choose_Find_ID_PW.class);
                startActivity(intent);
            }
        });
    }
}