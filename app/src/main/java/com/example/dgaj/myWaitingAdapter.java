package com.example.dgaj;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myWaitingAdapter extends RecyclerView.Adapter<myWaitingAdapter.ViewHolder> {

    private ArrayList<myWaiting> myWaitinglist;
    private List<Restaurant> restaurantList = new ArrayList<>();

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Context context;
    private String uid;


    public myWaitingAdapter(ArrayList<myWaiting> my_waitinglist, List<Restaurant> RestaurantList, Context context, String uid) {
        myWaitinglist = my_waitinglist;
        restaurantList = RestaurantList;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public myWaitingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_waiting_list_item, parent, false);
        myWaitingAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull myWaitingAdapter.ViewHolder holder, int position) {

        holder.customer_wait_rest_name.setText(myWaitinglist.get(position).getWait_rest_name());
        holder.customer_wait_rest_addr.setText(String.valueOf(myWaitinglist.get(position).getWait_rest_addr()));
        holder.customer_wait_rest_FD.setText(String.valueOf(myWaitinglist.get(position).getWait_rest_FD()));
        String RES_ID = String.valueOf(myWaitinglist.get(position).getWait_RES_ID());

        holder.btn_wait_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                Toast.makeText(context.getApplicationContext(), "내 대기정보 버튼", Toast.LENGTH_SHORT).show();

                String res_id = restaurantList.get(clickedPosition).getOpenData_id();

                DocumentReference docRef = mStore.collection(FirebaseID.rest_waiting_list).document(res_id);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot my_doc = task.getResult();
                                    if (my_doc.exists()) {
                                        List list = (List) my_doc.getData().get("list");
                                        if (list != null) {
                                            for (int i = 0; i < list.size(); i++) {

                                                Map<String, Object> map = (Map<String, Object>) list.get(i);
                                                if (map != null) {
                                                    String check_uid = (String) map.get("uid");
                                                    if (check_uid.equals(uid)) {

                                                        // Map에서 데이터 추출
                                                        //String my_rest_id = (String) map.get("restaurantOpenData_id");
                                                        String waiting_name = (String) map.get("wait_name");
                                                        String waiting_num = (String) map.get("wait_num");
                                                        String waiting_tel = (String) map.get("wait_tel");

                                                        String result = "대기자 이름 : " + waiting_name + "\n대기 인원수 : " + waiting_num
                                                                + "\n대기자 전화번호 : " + waiting_tel;

                                                        AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                                        ad.setIcon(R.drawable.ic_check);
                                                        ad.setTitle("< 나의 대기 정보 확인하기 >");
                                                        ad.setMessage(result);

                                                        ad.setPositiveButton("가게 상세정보", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                Restaurant selectedRestaurant = restaurantList.get(clickedPosition);
                                                                Intent intent = new Intent(context.getApplicationContext(), Customer_restaurantDetail.class);
                                                                intent.putExtra("uid", uid);
                                                                intent.putExtra("uid", uid);
                                                                intent.putExtra("restaurantName", selectedRestaurant.getName());
                                                                intent.putExtra("restaurantPhoneNumber", selectedRestaurant.getPhoneNumber());
                                                                intent.putExtra("restaurantAddress", selectedRestaurant.getAddress());
                                                                intent.putExtra("restaurantCuisine", selectedRestaurant.getCuisine());
                                                                intent.putExtra("restaurantDescription", selectedRestaurant.getDescription());      // 상세 설명 필드 추가
                                                                intent.putExtra("restaurantBusinesshour", selectedRestaurant.getBusinesshour()); // 카테고리 필드 추가
                                                                intent.putExtra("restaurantTopMenu", selectedRestaurant.getTopMenu());             // 대표메뉴 필드 추가
                                                                intent.putExtra("restaurantOpenData_id", selectedRestaurant.getOpenData_id());  // 가게고유번호 필드 추가
                                                                intent.putExtra("restaurantPickCount", selectedRestaurant.getDistance());
                                                                context.startActivity(intent);

                                                            }
                                                        });
                                                        ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Toast.makeText(context.getApplicationContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                        ad.show();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 업데이트 실패 처리
                                Toast.makeText(context.getApplicationContext(), "입장 처리 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        holder.btn_wait_delete_my_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "대기 삭제 버튼", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
                ad1.setIcon(R.drawable.ic_warning);
                ad1.setTitle("< 확인 >");
                ad1.setMessage("해당 대기를 취소하시겠습니까?");

                ad1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // position 변수를 사용하여 현재 아이템의 위치 확인 가능
                        int clickedPosition = holder.getAdapterPosition();

                        String nowAddress = restaurantList.get(clickedPosition).getAddress();
                        String[] addressParts = nowAddress.split(" ");


                        Map<String, Object> cust_itemToRemove = new HashMap<>();
                        cust_itemToRemove.put("opendata_id", RES_ID);
                        cust_itemToRemove.put("restaurant_addr_gu", addressParts[1]);

                        // cust_waiting_list 에서 position 값 삭제
                        mStore.collection(FirebaseID.cust_waiting_list).document(uid)
                                .update("my_waiting_list", FieldValue.arrayRemove(cust_itemToRemove))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context.getApplicationContext(), "cust_FireStore 반영 완료", Toast.LENGTH_SHORT).show();

                                        DocumentReference docRef = mStore.collection(FirebaseID.rest_waiting_list).document(RES_ID);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Long queue = document.getLong("queue");
                                                        --queue;
                                                        Map<String, Object> wait_queueMap = new HashMap<>();
                                                        wait_queueMap.put("queue", queue);

                                                        docRef.set(wait_queueMap, SetOptions.merge());

                                                        //rest_waiting_list에서 position 값 지우기
                                                        Map<String, Object> rest_itemToRemove = new HashMap<>();

                                                        if (document.exists()) { // 문서가 존재하는 경우에만 데이터 처리
                                                            List list = (List) document.getData().get("list");

                                                            if (list != null) {
                                                                for (int i = 0; i < list.size(); i++) {
                                                                    Map<String, Object> map = (Map<String, Object>) list.get(i);
                                                                    if (map != null) {
                                                                        //position과 uid가 같은 대기자 찾기
                                                                        if (uid.equals((String) map.get("uid"))) {

                                                                            String waiting_name = (String) map.get("wait_name");
                                                                            String waiting_num = (String) map.get("wait_num");
                                                                            String waiting_tel = (String) map.get("wait_tel");

                                                                            rest_itemToRemove.put("restaurantOpenData_id", RES_ID);
                                                                            rest_itemToRemove.put("uid", uid);
                                                                            rest_itemToRemove.put("wait_name", waiting_name);
                                                                            rest_itemToRemove.put("wait_num", waiting_num);
                                                                            rest_itemToRemove.put("wait_tel", waiting_tel);
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        // 해당 키의 Map이 존재하지 않을 경우 종료
                                                                        Toast.makeText(context.getApplicationContext(), "해당 KEY의 Map이 없습니다", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }

                                                            }
                                                            mStore.collection(FirebaseID.rest_waiting_list)
                                                                    .document(RES_ID).update("list", FieldValue.arrayRemove(rest_itemToRemove))
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(context.getApplicationContext(), "rest_FireStore 반영 완료", Toast.LENGTH_SHORT).show();

                                                                            // 업데이트에 성공하면, RecyclerView에서도 해당 아이템을 제거
                                                                            myWaitinglist.remove(clickedPosition);
                                                                            notifyItemRemoved(clickedPosition);
                                                                            Toast.makeText(context.getApplicationContext(), "대기 취소 완료", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 업데이트 실패 처리
                                        Toast.makeText(context.getApplicationContext(), "삭제 처리 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                ad1.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context.getApplicationContext(), "대기 삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                ad1.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (myWaitinglist != null ? myWaitinglist.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView customer_wait_rest_name;
        public TextView customer_wait_rest_addr;
        public TextView customer_wait_rest_FD;
        public Button btn_wait_my_info;
        public Button btn_wait_delete_my_wait;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            customer_wait_rest_name = itemView.findViewById(R.id.customer_wait_rest_name);
            customer_wait_rest_addr = itemView.findViewById(R.id.customer_wait_rest_addr);
            customer_wait_rest_FD = itemView.findViewById(R.id.customer_wait_rest_fD);
            btn_wait_my_info = itemView.findViewById(R.id.btn_wait_my_info);
            btn_wait_delete_my_wait = itemView.findViewById(R.id.btn_delete_my_wait);
        }
    }
}
