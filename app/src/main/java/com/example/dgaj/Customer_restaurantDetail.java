package com.example.dgaj;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer_restaurantDetail extends AppCompatActivity {
    private Button btn_lineup;

    private ToggleButton btn_pick;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private ImageView donating_angel;
    private boolean isOpendataIdExist;
    private static final String TAG = "Customer_restaurantDetail";
    private String uid;
    private String restaurantOpenData_id;
    private String address_gu;

    private void updateLoveCount(String restaurantOpenDataId, TextView txtShopCntPicked) {
        DocumentReference restLoveListRef = mStore.collection(FirebaseID.rest_love_list).document(restaurantOpenDataId);
        restLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // "totallove" 필드가 있는지 확인
                        if (document.contains("totallove")) {
                            long totalLove = (long) document.get("totallove");
                            // "totallove" 값을 TextView에 설정
                            txtShopCntPicked.setText("찜: " + totalLove);
                        }
                    } else {
                        // 문서가 없을 때의 처리
                        // 예를 들어, "찜 개수: 0"으로 표시하거나 다른 디폴트 값으로 설정할 수 있습니다.
                        txtShopCntPicked.setText("찜: 0");
                    }
                } else {
                    // 데이터 불러오기 실패 시의 처리
                    // 예를 들어, 에러 메시지를 토스트로 표시할 수 있습니다.
                    Toast.makeText(getApplicationContext(), "찜 개수 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_restaurant_detail);

        btn_lineup = findViewById(R.id.btn_lineup);
        btn_pick = findViewById(R.id.btn_pick);


//        //lottie animation view 리소스 아이디 연결
//        btn_picked = (LottieAnimationView) findViewById(R.id.btn_pick);
//
//        btn_picked.setAnimation("heart.json");
//        btn_picked.loop(true);
//        btn_picked.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!btn_picked_is_clicked){
//                    // 애니메이션을 한번 실행시킨다.
//                    // Custom animation speed or duration.
//                    // ofFloat(시작 시간, 종료 시간).setDuration(지속시간)
//                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(1000);
//
//                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            btn_picked.setProgress((Float) animation.getAnimatedValue());
//                        }
//                    });
//                    animator.start();
//                    btn_picked_is_clicked = true;
//
//                }
//                else {
//                    // 애니메이션을 한번 실행시킨다.
//                    // Custom animation speed or duration.
//                    // ofFloat(시작 시간, 종료 시간).setDuration(지속시간)
//                    ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(1000);
//
//                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            btn_picked.setProgress((Float) animation.getAnimatedValue());
//                        }
//                    });
//                    animator.start();
//                    btn_picked_is_clicked = false;
//
//                }
//
//            }
//        });

        // 인텐트로 전달된 데이터 가져오기
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        String restaurantName = intent.getStringExtra("restaurantName").replaceAll("<br />", "\n");
        String restaurantPhoneNumber = intent.getStringExtra("restaurantPhoneNumber").replaceAll("<br />", "\n");
        String restaurantAddress = intent.getStringExtra("restaurantAddress").replaceAll("<br />", "\n");
        String restaurantDescription = intent.getStringExtra("restaurantDescription").replaceAll("<br />", "\n");
        String restaurantCuisine = intent.getStringExtra("restaurantCuisine").replaceAll("<br />", "\n");
        String restaurantTopMenu = intent.getStringExtra("restaurantTopMenu").replaceAll("<br />", "\n");
        restaurantOpenData_id = intent.getStringExtra("restaurantOpenData_id");

        //가게 정보에 기부천사 마크 띄우기
        donating_angel = findViewById(R.id.donating_angel);
        donating_angel.setVisibility(View.GONE); // 이미지뷰를 숨겨 놓음

        mStore.collection("id_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // 모든 문서에 대한 반복
                            for (DocumentSnapshot document : task.getResult()) {
                                // 'donatingAngel' 필드 확인
                                if (document.contains("donatingAngel")) {
                                    String owner_opendataId = document.getString("opendata_id");

                                    // 'owner_opendataId'와 'restaurantOpenData_id'가 일치하는지 확인
                                    if (owner_opendataId != null && owner_opendataId.equals(restaurantOpenData_id)) {
                                        // 일치하는 경우 이미지뷰를 보이게 설정
                                        donating_angel.setVisibility(View.VISIBLE);
                                        break; // 이미지뷰를 설정했으므로 반복 종료
                                    }
                                }
                            }
                        }
                    }
                });

        // XML 레이아웃의 TextView 요소에 데이터 설정
        TextView txt_shop_name = findViewById(R.id.txt_shop_name);
        TextView txt_shop_phonenumber = findViewById(R.id.txt_shop_phonenumber);
        TextView txt_shop_address = findViewById(R.id.txt_shop_address);
        TextView txt_shop_intro = findViewById(R.id.txt_shop_intro);
        TextView txt_shop_cuisine = findViewById(R.id.txt_shop_cuisine);
        TextView txt_shop_top_menu = findViewById(R.id.txt_shop_top_menu);
        TextView txt_shop_cnt_picked = findViewById(R.id.txt_shop_cnt_picked);


        txt_shop_name.setText("이름: " + restaurantName);
        txt_shop_phonenumber.setText("전화번호: " + restaurantPhoneNumber);
        txt_shop_address.setText("주소: " + restaurantAddress);
        txt_shop_intro.setText("상세 설명: " + restaurantDescription);
        txt_shop_cuisine.setText("카테고리: " + restaurantCuisine);
        txt_shop_top_menu.setText("대표메뉴: " + restaurantTopMenu);

        String[] addressParts = restaurantAddress.split(" ");
        address_gu = addressParts[1];

        //restaurantOpenData_id를 사용하여 해당 문서의 totallove 값을 불러오기
        updateLoveCount(restaurantOpenData_id, txt_shop_cnt_picked);

        //토글버튼
        mStore.collection(FirebaseID.cust_love_list).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // 문서 가져오기
                    DocumentSnapshot document = task.getResult();
                    // 문서에 my_love_list 필드가 있는지 확인
                    if (document.contains("my_love_list")) {
                        List<Map<String, Object>> myLoveList = (List<Map<String, Object>>) document.get("my_love_list");

                        // 가게가 찜 목록에 이미 있는 경우
                        for (Map<String, Object> item : myLoveList) {
                            if (item.containsKey("opendata_id") && item.get("opendata_id").equals(restaurantOpenData_id)) {
                                // 버튼을 클릭된 상태로 설정
                                if (btn_pick instanceof ToggleButton) {
                                    ((ToggleButton) btn_pick).toggle();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });

        btn_pick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private boolean isRestaurantInMyLoveList(List<Map<String, Object>> myLoveList, String restaurantId) {
                if (myLoveList != null) {
                    for (Map<String, Object> item : myLoveList) {
                        String opendataId = (String) item.get("opendata_id");
                        if (opendataId != null && opendataId.equals(restaurantId)) {
                            // 가게가 찜 목록에 이미 있는 경우
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {// 토글버튼이 클릭된 상태일 때의 동작
                    handleToggleOn();
                } else {// 토글버튼이 클릭되지 않은 상태일 때의 동작
                    handleToggleOff();

                }
            }

            private void handleToggleOn() {
                // 나머지 코드는 이전과 동일하게 유지
                Map<String, Object> loveMap = new HashMap<>();
                loveMap.put("address_gu", address_gu);
                loveMap.put("opendata_id", restaurantOpenData_id);


                // 컬렉션 참조
                final DocumentReference custLoveListRef = mStore.collection(FirebaseID.cust_love_list).document(uid);
                final DocumentReference restLoveListRef = mStore.collection(FirebaseID.rest_love_list).document(restaurantOpenData_id);

                // 문서가 있는지 확인
                custLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                // 문서가 없으면 생성
                                custLoveListRef.set(new HashMap<>());
                            }

                            // 문서 가져오기
                            DocumentSnapshot document = task.getResult();

                            // 문서 업데이트
                            custLoveListRef.update("my_love_list", FieldValue.arrayUnion(loveMap)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        List<Map<String, Object>> myLoveList = (List<Map<String, Object>>) document.get("my_love_list");

                                        // my_love_list에서 restaurantOpenData_id 값이 있는지 확인
                                        if (isRestaurantInMyLoveList(myLoveList, restaurantOpenData_id)) {
                                            Toast.makeText(getApplicationContext(), "이미 찜한 식당입니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // 문서가 있는지 확인
                                            restLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().exists()) {
                                                            // 문서가 없으면 생성
                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("totallove", 1);
                                                            data.put("address_gu", address_gu);
                                                            data.put("opendata_id", restaurantOpenData_id);
                                                            restLoveListRef.set(data);
                                                            updateLoveCount(restaurantOpenData_id, txt_shop_cnt_picked);
                                                        } else {
                                                            // 문서가 이미 있는 경우 totallove를 1씩 증가
                                                            restLoveListRef.update("totallove", FieldValue.increment(1));
                                                            updateLoveCount(restaurantOpenData_id, txt_shop_cnt_picked);
                                                        }

                                                        Toast.makeText(getApplicationContext(), "찜하기 성공", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "찜하기 실패", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }

            private void handleToggleOff() {
                // 클릭되지 않은 상태일 때의 추가적인 로직을 여기에 추가
                Map<String, Object> loveMapRemove = new HashMap<>();
                loveMapRemove.put("address_gu", address_gu);
                loveMapRemove.put("opendata_id", restaurantOpenData_id);


                // 컬렉션 참조
                final DocumentReference custLoveListRef = mStore.collection(FirebaseID.cust_love_list).document(uid);
                final DocumentReference restLoveListRef = mStore.collection(FirebaseID.rest_love_list).document(restaurantOpenData_id);

                // 문서가 있는지 확인
                custLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 문서 가져오기
                            DocumentSnapshot document = task.getResult();

                            // 문서 업데이트
                            custLoveListRef.update("my_love_list", FieldValue.arrayRemove(loveMapRemove)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "찜 취소", Toast.LENGTH_SHORT).show();
                                        List<Map<String, Object>> myLoveList = (List<Map<String, Object>>) document.get("my_love_list");

                                        restLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (!task.getResult().exists()) {
                                                        updateLoveCount(restaurantOpenData_id, txt_shop_cnt_picked);
                                                    } else {
                                                        // 문서가 이미 있는 경우 totallove를 1씩 감소
                                                        restLoveListRef.update("totallove", FieldValue.increment(-1));
                                                        updateLoveCount(restaurantOpenData_id, txt_shop_cnt_picked);
                                                    }
                                                } else {
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        btn_lineup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //대기 열렸는지
                mStore.collection(FirebaseID.rest_waiting_list).document(restaurantOpenData_id)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.e(TAG, "restaurantOpenData_id = "+restaurantOpenData_id);
                                if (document.exists()) { //대기 열림

                                    //사용자 대기중복&대기개수 확인
                                    mStore.collection(FirebaseID.cust_waiting_list)
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(task_1 -> {
                                                if (task_1.isSuccessful()) {
                                                    DocumentSnapshot custWaitingDocument = task_1.getResult();
                                                    if (custWaitingDocument.exists()) {
                                                        List<Map<String, Object>> existingResList = (List<Map<String, Object>>) custWaitingDocument.get("my_waiting_list");

                                                        //대기 식당 중복 체크
                                                        isOpendataIdExist = false;
                                                        if (existingResList != null) {
                                                            for (Map<String, Object> resMap : existingResList) {
                                                                String existingOpendataId = (String) resMap.get("opendata_id");
                                                                if (existingOpendataId != null && existingOpendataId.equals(restaurantOpenData_id)) {
                                                                    isOpendataIdExist = true;
                                                                    Toast.makeText(getApplicationContext(), "이미 대기 신청한 식당입니다", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }
                                                            }
                                                            if (existingResList != null && existingResList.size() >= 5 && isOpendataIdExist == false) {
                                                                // my_waiting_list에 있는 요소 개수 확인
                                                                Toast.makeText(getApplicationContext(), "이미 5개 이상의 식당에 대기 중입니다.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // my_waiting_list에 opendata_id가 존재하지 않으면 대기 가능
                                                                if (isOpendataIdExist == false) {
                                                                    String address_gu = "";
                                                                    String[] addressParts = restaurantAddress.split(" ");       //"대한민국 대구광역시 수성구 교학로 3"의 경우 " "를 기준으로 끊었을 때 수성구의 값이 address_gu에 입력됨
                                                                    if (addressParts.length > 2) {
                                                                        address_gu = addressParts[1];
                                                                    }

                                                                    Intent intent = new Intent(Customer_restaurantDetail.this, Customer_applyWaiting.class);
                                                                    intent.putExtra("uid", uid);
                                                                    intent.putExtra("restaurantOpenData_id", restaurantOpenData_id);
                                                                    intent.putExtra("restaurantAddr_gu", address_gu);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        }


                                                    }

                                                }

                                            });
                                } else {
                                    AlertDialog.Builder ad = new AlertDialog.Builder(Customer_restaurantDetail.this);
                                    ad.setIcon(R.drawable.ic_warning);
                                    ad.setTitle("Waiting List is closed.");
                                    ad.setMessage("현재 대기를 신청할 수 없습니다.");

                                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    ad.show();
                                }
                            } else {
                                Toast.makeText(Customer_restaurantDetail.this, "대기창 유무 확인 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
    }
}