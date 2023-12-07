package com.example.dgaj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Owner_happyDonation extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonatingAdapter donatingAdapter;
    private ArrayList<Donating> arrayList = new ArrayList<>();

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_happy_donation);

        // Firebase Firestore 데이터 가져오기
        mStore.collection("children_center_list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        String name = document.getId();
                        String addr = document.getString("address");
                        String tel = "";    // 현재 tel은 필드에 없기 때문에 null

                        int drawableResourceId = 0;

                        if (name.equals("구름아동센터")) {
                            // 리소스 ID 얻기
                            drawableResourceId = R.drawable.cloud;
                        } else if (name.equals("별님아동센터")) {
                            drawableResourceId = R.drawable.emptystar;
                        } else if (name.equals("미소아동센터")) {
                            drawableResourceId = R.drawable.smile;
                        }

                        // Firebase에서 가져온 데이터를 ArrayList에 추가
                        Donating donating = new Donating(name, addr, tel, drawableResourceId);
                        arrayList.add(donating);
                    }

                    // RecyclerView 설정
                    recyclerView = findViewById(R.id.recyclerView_donating);
                    donatingAdapter = new DonatingAdapter(arrayList, Owner_happyDonation.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Owner_happyDonation.this));
                    recyclerView.setAdapter(donatingAdapter);
                } else {
                    Log.e("FirestoreExample", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}