package com.example.dgaj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Owner_mainScreen extends AppCompatActivity {
    private Button menu;
    private Button btn_res_close;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private WaitingAdapter adapter;
    private ArrayList<Waiting> arrayList = new ArrayList<>();
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    String RES_ID;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner_main_screen);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        RES_ID = intent.getStringExtra("RES_ID");

        menu = findViewById(R.id.menu_btn);
        btn_res_close = findViewById(R.id.btn_res_close);

        drawerLayout = findViewById(R.id.owner_main_screen);

        adapter = new WaitingAdapter(arrayList, this, RES_ID);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view_owner);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                int id = item.getItemId();

                if (id == R.id.my_account) {
                    Intent intent = new Intent(getApplicationContext(), Customer_myAccount.class);
                    startActivity(intent);
                } else if (id == R.id.happy_donation) {
                    Intent intent = new Intent(getApplicationContext(), Owner_happyDonation.class);
                    startActivity(intent);
                } else if(id==R.id.record_donation) {
                    Intent intent = new Intent(getApplicationContext(), Owner_donation_record.class);
                    startActivity(intent);
                }else if (id == R.id.logout) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView_waiting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 데이터를 추가하기 전에 arrayList를 초기화
        arrayList.clear();

        // Firestore에서 데이터 가져오기
        DocumentReference docRef = mstore.collection(FirebaseID.rest_waiting_list).document(RES_ID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot wait_doc = task.getResult();

                if (wait_doc.exists()) { // 문서가 존재하는 경우에만 데이터 처리
                    List list = (List) wait_doc.getData().get("list");
                    if(list!=null){
                        for (int i = 0; i < list.size(); i++) {
                            // "i"라는 키로 이루어진 Map 가져오기
                            //Map<String, Object> map = (Map<String, Object>) wait_doc.get(String.valueOf(i));
                            Map<String, Object> map = (Map<String, Object>) list.get(i);
                            //HashMap map = (HashMap) list.get(i);
                            if (map != null) {
                                // Map에서 데이터 추출
                                String waiting_name = (String) map.get("wait_name");
                                String waiting_num = (String) map.get("wait_num");
                                String waiting_tel = (String) map.get("wait_tel");
                                String cust_uid = (String) map.get("uid");

                                // Waiting 객체 생성
                                Waiting waiting = new Waiting(waiting_name, waiting_num, waiting_tel, cust_uid);
                                // 리스트에 추가
                                arrayList.add(waiting);
                            } else {
                                // 해당 키의 Map이 존재하지 않을 경우 종료
                                Toast.makeText(Owner_mainScreen.this, "해당 KEY의 Map이 없습니다", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }

                    // 어댑터에 데이터 변경을 알리기
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Owner_mainScreen.this, "문서가 없습니다", Toast.LENGTH_SHORT).show();
                    // 문서가 존재하지 않을 경우 처리
                }
            } else {
                // Firestore에서 문서를 가져오는 데 실패한 경우 처리
                Toast.makeText(Owner_mainScreen.this, "문서를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        btn_res_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(Owner_mainScreen.this);
                ad.setIcon(R.drawable.ic_warning);
                ad.setTitle("대기창 닫기");
                ad.setMessage("대기창을 닫으시겠습니까?");

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 레스토랑 문서에 대기 목록을 업데이트합니다.
                        mstore.collection(FirebaseID.rest_waiting_list)
                                .document(RES_ID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "대기창 닫기 완료", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Owner_mainScreen.this, Owner_mainScreen_unopened.class);
                                        intent.putExtra("uid", uid);
                                        intent.putExtra("RES_ID", RES_ID);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "대기창 닫기 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "대기창 닫기 최소", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                ad.show();
            }
        });

    }
}