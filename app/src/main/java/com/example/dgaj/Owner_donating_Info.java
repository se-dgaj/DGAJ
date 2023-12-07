package com.example.dgaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Owner_donating_Info extends AppCompatActivity {
    private Button button;
    private EditText name_sender, article_to_donate, cnt_article, tel_donating01, tel_donating02;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        name_sender = findViewById(R.id.name_sender);
        article_to_donate = findViewById(R.id.article_to_donate);
        cnt_article = findViewById(R.id.cnt_article);
        tel_donating01 = findViewById(R.id.tel_donating01);
        tel_donating02 = findViewById(R.id.tel_donating02);

        button = findViewById(R.id.btn_ok_donating);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_sender.getText().toString().trim();
                final String article = article_to_donate.getText().toString().trim();
                final String cnt = cnt_article.getText().toString().trim().replaceAll("[^0-9]", "");
                final String tel = "010" + tel_donating01.getText().toString().trim().replaceAll("[^0-9]", "") + tel_donating01.getText().toString().trim().replaceAll("[^0-9]", "");

                if (!name.isEmpty() && !article.isEmpty() && !cnt.isEmpty() && !tel.isEmpty()) {

                    //센터 리스트에 넣을 기부 정보
                    Map<String, Object> donating_center = new HashMap<>();
                    donating_center.put("name", name);
                    donating_center.put("article", article);
                    donating_center.put("cnt", cnt);
                    donating_center.put("tel", tel);

                    Intent intent = getIntent();
                    if (intent != null) {
                        //intent로 센터 이름 받기
                        String center_name = intent.getStringExtra("name");

                        //파이어스토어 문서 참조-uid 리스트에 기부 내역 추가
                        FirebaseUser user = mAuth.getCurrentUser();

                        Map<String, Object> donating_uid = new HashMap<>();
                        donating_uid.put("article", article);
                        donating_uid.put("center_name", center_name);
                        donating_uid.put("cnt", cnt);
                        //mStore.collection("donating_uid_list").document(user.getUid()).set(donating_uid, SetOptions.merge());

                        DocumentReference docRef = mStore.collection("donating_uid_list").document(user.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> donatingUidMap = new HashMap<>();
                                    ArrayList<Map<String, Object>> donatingUid_info;

                                    // uid리스트 내 donating_info 필드가 있을 경우
                                    if (task.getResult().exists() && task.getResult().contains("donating_info_" + name)) {
                                        donatingUid_info = (ArrayList<Map<String, Object>>) task.getResult().get("donating_info_" + name);

                                        // uid리스트 내 donating_info 필드가 없을 경우 생성
                                        if (donatingUid_info == null) {
                                            donatingUid_info = new ArrayList<>();
                                        }

                                        donatingUid_info.add(donating_uid);
                                    } else {
                                        // uid 문서 자체가 없을 경우, 새로 생성
                                        donatingUid_info = new ArrayList<>();
                                        donatingUid_info.add(donating_uid);
                                    }

                                    //map 업데이트
                                    donatingUidMap.put("donating_info_" + name, donatingUid_info);
                                    //문서 업데이트
                                    //docRef.update(donatingUidMap);
                                    docRef.set(donatingUidMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Owner_donating_Info.this, "uid list 업데이트 완료.", Toast.LENGTH_SHORT).show();
                                            // 여기서 길이를 읽어오도록 처리
                                            readLength();
                                        }
                                    });

                                }
                            }
                        });

                        //파이어스토어 문서 참조-센터 리스트에 기부 내역 추가
                        DocumentReference docRef2 = mStore.collection("children_center_list").document(center_name);
                        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> donatingMap = new HashMap<>();
                                    ArrayList<Map<String, Object>> donating_info;

                                    // 센터 내 donating_info 필드가 있을 경우
                                    if (task.getResult().exists() && task.getResult().contains("donating_info")) {
                                        donating_info = (ArrayList<Map<String, Object>>) task.getResult().get("donating_info");
                                        donating_info.add(donating_center);
                                    }
                                    // 센터 내 donating_info 필드가 없을 경우 생성
                                    else {
                                        donating_info = new ArrayList<>();
                                        donating_info.add(donating_center);
                                    }

                                    //map 업데이트
                                    donatingMap.put("donating_info", donating_info);
                                    //문서 업데이트
                                    docRef2.update(donatingMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Owner_donating_Info.this, center_name + "로 기부 완료되었습니다.", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(Owner_donating_Info.this, Owner_mainScreen.class);
//                                            startActivity(intent);
                                        }
                                    });

                                }
                            }
                        });

                    } else {
                        Toast.makeText(Owner_donating_Info.this, "빈칸 없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    }

                }
                ;
            }
            ;
        });
    }

    ;
    //길이 읽기 코드 > 기부천사 마킹 위한 ~~
    private void readLength() {
        FirebaseUser user = mAuth.getCurrentUser();
        mStore.collection("donating_uid_list").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // uid 문서 기준 필드 내 모든 배열의 길이 구하기
                                int totalLength = 0;
                                for (String field : document.getData().keySet()) {
                                    if (document.get(field) instanceof List) {
                                        List<Object> list = (List<Object>) document.get(field);
                                        totalLength += list.size();
                                    }
                                }

                                //기부 횟수가 3회 이상이면 idlist 컬렉션 내 uid 문서에 기부천사 필드 넣기
                                if (totalLength > 3){
                                    Map<String, Object> donatingAngel = new HashMap<>();
                                    donatingAngel.put("donatingAngel", true);
                                    mStore.collection("id_list").document(user.getUid()).update(donatingAngel);
                                }
                                Intent intent = new Intent(Owner_donating_Info.this, Owner_mainScreen.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }


}

