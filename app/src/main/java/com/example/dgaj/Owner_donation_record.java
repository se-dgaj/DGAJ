package com.example.dgaj;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Owner_donation_record extends AppCompatActivity {

    long mNow;
    Date mdate;
    TimeZone tz;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    TextView date_record_donation;
    TextView countDonationTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    private List<Donation> donationList = new ArrayList<>();
    private DonationAdapter donationAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_record_donation);
        date_record_donation = findViewById(R.id.date_record_donation);
        countDonationTextView = findViewById(R.id.count_donation);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            loadDataFromFirestore();
        }

        // 어댑터 초기화
        donationAdapter = new DonationAdapter(donationList);

        // RecyclerView에 어댑터 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView_record_donation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(donationAdapter);
        tz=TimeZone.getTimeZone("Asia/Seoul");

        date_record_donation.setText(getTime());
    }

    private void loadDataFromFirestore() {
        DocumentReference docRef = db.collection("donating_uid_list").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> donatingInfo = document.getData();
                        if (donatingInfo != null) {
                            int totalDonationCount = 0;
                            for (Map.Entry<String, Object> entry : donatingInfo.entrySet()) {
                                String key = entry.getKey();
                                if (key.startsWith("donating_info")) {
                                    try {
                                        Object value = entry.getValue();
                                        if (value instanceof ArrayList) {
                                            ArrayList<Map<String, Object>> donationDataList = (ArrayList<Map<String, Object>>) value;
                                            for (Map<String, Object> donationData : donationDataList) {
                                                String nameKey = key.replace("donating_info_", ""); // 이름 키 추출
                                                String article = donationData.get("article").toString();
                                                String centerName = donationData.get("center_name").toString();
                                                String cnt = donationData.get("cnt").toString();

                                                Donation donation = new Donation();
                                                donation.setDonorName("기부자 이름: "+nameKey); // 기부자 이름 설정
                                                donation.setArticle("물품: " + article);
                                                donation.setCenterName("센터 이름: " + centerName);
                                                donation.setCnt("개수: " + cnt);


                                                donationList.add(donation);
                                            }

                                            // 행복 횟수를 증가시킴
                                            totalDonationCount += donationDataList.size();
                                        }
                                    } catch (Exception e) {
                                        Log.e("DonationRecordError", "Error while parsing donation data: " + e.getMessage());
                                    }
                                }
                            }

                            donationAdapter.notifyDataSetChanged();

                            // 행복 횟수를 TextView에 표시
                            countDonationTextView.setText(String.valueOf(totalDonationCount));
                        }
                    } else {
                        // 문서가 존재하지 않을 경우 처리할 내용 작성
                    }
                } else {
                    // 작업 실패 시 처리할 내용 작성
                    Log.e("DonationRecordError", "Failed to fetch data from Firestore: " + task.getException());
                }
            }
        });
    }


    private String getTime() {
        mFormat.setTimeZone(tz);
        mNow = System.currentTimeMillis();
        mdate = new Date(mNow);
        return mFormat.format(mdate);
    }
}
