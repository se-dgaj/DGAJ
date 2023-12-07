package com.example.dgaj;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Customer_changeTel extends AppCompatActivity {

    private EditText et_existTel;
    private EditText et_newTel;
    private EditText et_checkTel;

    private Button btn_checkTel;

    private Button btn_ok;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //파이어베이스 인증 받고
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance(); //인스턴스 가져옴


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tel);

        et_existTel = findViewById(R.id.et_existTel);
        et_newTel = findViewById(R.id.et_newTel);
        et_checkTel = findViewById(R.id.et_checkTel);
        btn_checkTel = findViewById(R.id.btn_checkTel);
        btn_ok = findViewById(R.id.btn_ok);


        btn_checkTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ButtonClicked", "Button clicked!");

                final String exist_Tel = et_existTel.getText().toString().trim().replaceAll("[^0-9]", "");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();

                mStore.collection(FirebaseID.id_list).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String tel = document.getString(FirebaseID.tel);


                                if (tel != null) {


                                    if (tel.equals(exist_Tel)) {
                                        Toast.makeText(Customer_changeTel.this, "기존 전화번호가 일치합니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Customer_changeTel.this, "기존 전화번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    }


                                } else {

                                    Toast.makeText(Customer_changeTel.this, "기존 전화번호가 NULL입니다.", Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                Toast.makeText(Customer_changeTel.this, "문서에 전화번호 필드가 없습니다", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e("FirestoreQuery", "Query failed: " + exception.getMessage());
                            }
                        }
                    }
                });
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ButtonClicked", "Button clicked!");

                final String exist_Tel = et_existTel.getText().toString().trim().replaceAll("[^0-9]", "");

                final String newTel = et_newTel.getText().toString().trim().replaceAll("[^0-9]", "");

                final String checkTel = et_checkTel.getText().toString().trim().replaceAll("[^0-9]", "");

                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();

                mStore.collection(FirebaseID.id_list).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String tel = document.getString(FirebaseID.tel);


                                if (tel != null) {
                                    if (exist_Tel.length() == 11 && exist_Tel.startsWith("010") && newTel.length() == 11 && newTel.startsWith("010") && checkTel.length() == 11 && checkTel.startsWith("010")) {
                                        if (tel.equals(exist_Tel)) {

                                            if (exist_Tel.equals(newTel)) {
                                                Toast.makeText(Customer_changeTel.this, "기존 전화번호가 변경할 전화번호와 같습니다.", Toast.LENGTH_SHORT).show();
                                            } else if (newTel.equals(checkTel)) {

                                                FirebaseUser user = mAuth.getCurrentUser();


                                                DocumentReference userDocRef = mStore.collection(FirebaseID.id_list).document(userID); //인증 update후 store 업데이트
                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put(FirebaseID.tel, newTel);
                                                userDocRef.update(updates);
                                                finish();
                                                Toast.makeText(Customer_changeTel.this, "전화번호가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                                            } else {

                                                Toast.makeText(Customer_changeTel.this, "새로운 전화번호가 확인 전화번호와 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(Customer_changeTel.this, "기존 전화번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(Customer_changeTel.this, "전화번호의 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(Customer_changeTel.this, "기존 전화번호가 NULL입니다.", Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                Toast.makeText(Customer_changeTel.this, "tel 필드가 없습니다", Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e("FirestoreQuery", "Query failed: " + exception.getMessage());
                            }
                        }
                    }
                });
            }
        });


    }
}