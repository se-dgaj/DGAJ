package com.example.dgaj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FindID extends AppCompatActivity {

    private EditText et_name, et_tel;
    private Button btn_find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        btn_find = findViewById(R.id.btn_find);
        et_name = findViewById(R.id.et_name);
        et_tel = findViewById(R.id.et_tel);

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_name.getText().toString().trim();
                final String tel = et_tel.getText().toString().trim();

                if (!name.isEmpty() && !tel.isEmpty()) {
                    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                    mStore.collection(FirebaseID.id_list)
                            .whereEqualTo(FirebaseID.name, name)
                            .whereEqualTo(FirebaseID.tel, tel)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            // 검색 결과가 있을 때
                                            for (DocumentSnapshot document : task.getResult()) {
                                                // 해당 문서이름(사용자의 uid) 가져오기
                                                String uid = document.getId();

                                                mStore.collection(FirebaseID.id_list).document(uid).get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        // uid 문서가 존재할 때 email 필드의 값을 가져오기
                                                                        String userId = document.getString("email");
                                                                        //Toast.makeText(FindID.this, userId, Toast.LENGTH_SHORT).show();

                                                                        // 아이디를 인텐트로 넘겨주기
                                                                        Intent intent = new Intent(FindID.this, Find_result.class);
                                                                        intent.putExtra("userId", userId);
                                                                        startActivity(intent);
                                                                    }
                                                                } else {
                                                                    // 작업이 실패한 경우의 예외 처리
                                                                    Toast.makeText(FindID.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            // 검색 결과가 없을 때
                                            Toast.makeText(FindID.this, "해당 정보와 일치하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // 검색 실패 시 예외 처리
                                        Toast.makeText(FindID.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(FindID.this, "빈칸 없이 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                ;
            }
            ;
        });
    }
}