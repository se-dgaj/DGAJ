package com.example.dgaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Owner_mainScreen_unopened extends AppCompatActivity {
    private Button menu;
    private Button btn_res_open;
    private DrawerLayout drawerLayout;

    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();

    private String RES_ID;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main_screen_unopened);

        menu = findViewById(R.id.menu_btn);
        drawerLayout = findViewById(R.id.owner_main_screen);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        RES_ID = intent.getStringExtra("RES_ID");

        btn_res_open = findViewById(R.id.btn_res_open);
        btn_res_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> waitingMap = new HashMap<>();
                waitingMap.put("queue", 0); //대기팀 수 확인하자

                mstore.collection(FirebaseID.rest_waiting_list).document(RES_ID).set(waitingMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(Owner_mainScreen_unopened.this, Owner_mainScreen.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("RES_ID", RES_ID);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Firestore에 업데이트 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

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
                } else if (id == R.id.record_donation) {
                    Intent intent = new Intent(getApplicationContext(), Owner_donation_record.class);
                    startActivity(intent);
                } else if (id == R.id.logout) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });


    }
}