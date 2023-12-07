package com.example.dgaj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Customer_applyWaiting extends AppCompatActivity {

    private EditText lineup_tel, lineup_name, reservation_num;
    private Button btn_confirm_line_up;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private Long queue;
    private String uid;
    private String restaurantOpenData_id;
    private String restaurantAddr_gu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_apply_waiting);
        lineup_tel = findViewById(R.id.lineup_tel);
        lineup_name = findViewById(R.id.lineup_name);
        reservation_num = findViewById(R.id.et_reservation_num);
        btn_confirm_line_up = findViewById(R.id.btn_confirm_line_up);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        restaurantOpenData_id = intent.getStringExtra("restaurantOpenData_id");
        restaurantAddr_gu = intent.getStringExtra("restaurantAddr_gu");

        btn_confirm_line_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection(FirebaseID.rest_waiting_list).document(restaurantOpenData_id);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            queue = document.getLong("queue");
                            if (queue != null) {
                                AlertDialog.Builder ad = new AlertDialog.Builder(Customer_applyWaiting.this);
                                ad.setIcon(R.drawable.ic_warning);
                                ad.setTitle("Question");
                                ad.setMessage("현재 대기 중인 대기팀 : " + queue + "팀\n대기 신청 하시겠습니까?");

                                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String wait_name = lineup_name.getText().toString().trim();
                                        final String wait_tel = lineup_tel.getText().toString().trim().replaceAll("[^0-9]", "");
                                        final String wait_num = reservation_num.getText().toString().trim();

                                        if (wait_name.isEmpty() || wait_num.isEmpty() || wait_tel.isEmpty()) {
                                            Toast.makeText(getApplicationContext(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (wait_tel.length() != 11) {
                                                Toast.makeText(getApplicationContext(), "전화번호 입력 오류", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (!wait_tel.startsWith("010")) {
                                                    Toast.makeText(getApplicationContext(), "전화번호 입력 오류", Toast.LENGTH_SHORT).show();
                                                } else {


                                                    Map<String, Object> waitingMap = new HashMap<>();
                                                    waitingMap.put("restaurantOpenData_id", restaurantOpenData_id);
                                                    waitingMap.put("uid", uid);
                                                    waitingMap.put(FirebaseID.wait_name, wait_name);
                                                    waitingMap.put(FirebaseID.wait_tel, wait_tel);
                                                    waitingMap.put(FirebaseID.wait_num, wait_num);

                                                    ++queue;

                                                    Map<String, Object> wait_queueMap = new HashMap<>();
                                                    wait_queueMap.put("queue", queue);

                                                    // 레스토랑 문서에 대기 목록을 업데이트합니다. (restaurant 버전)
                                                    mStore.collection(FirebaseID.rest_waiting_list)
                                                            .document(restaurantOpenData_id)
                                                            .update("list", FieldValue.arrayUnion(waitingMap))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mStore.collection(FirebaseID.rest_waiting_list)
                                                                            .document(restaurantOpenData_id)
                                                                            .set(wait_queueMap, SetOptions.merge());

                                                                    Map<String, Object> cust_waitingMap = new HashMap<>();
                                                                    cust_waitingMap.put("opendata_id", restaurantOpenData_id);
                                                                    cust_waitingMap.put("restaurant_addr_gu", restaurantAddr_gu);


                                                                    // 레스토랑 문서에 대기 목록을 업데이트합니다. (user 개인 대기 목록)
                                                                    mStore.collection(FirebaseID.cust_waiting_list)
                                                                            .document(uid)
                                                                            .update("my_waiting_list", FieldValue.arrayUnion(cust_waitingMap))
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(getApplicationContext(), "대기 신청 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                                                                    Intent intent = new Intent(Customer_applyWaiting.this, Customer_myWaiting.class);
                                                                                    intent.putExtra("uid", uid);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "대기 신청 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                            }
                                        }
                                    }
                                });
                                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "대기 신청 취소했습니다.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                                ad.show();
                            }
                        } else {
                            // 문서가 존재하지 않는 경우
                            Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        // 문서 가져오기 실패할 경우
                        Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }
}