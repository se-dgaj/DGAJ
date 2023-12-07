package com.example.dgaj;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FieldValue;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;

public class WaitingAdapter extends RecyclerView.Adapter<WaitingAdapter.ViewHolder> {
    private ArrayList<Waiting> Waitinglist;
    private Context context;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String RES_ID;
    private static final String TAG = "WaitingAdapter";

    public WaitingAdapter(ArrayList<Waiting> waitinglist, Context context, String RES_ID) {
        Waitinglist = waitinglist;
        this.context = context;
        this.RES_ID = RES_ID;
    }

    @NonNull
    @Override
    public WaitingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waitinglist_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingAdapter.ViewHolder holder, int position) {

        holder.customer_name.setText(Waitinglist.get(position).getWaiting_name());
        holder.customer_num_of_group.setText(String.valueOf(Waitinglist.get(position).getWaiting_num()));
        holder.customer_tel_number.setText(String.valueOf(Waitinglist.get(position).getWaiting_tel()));
        String cust_uid = String.valueOf(Waitinglist.get(position).getWaiting_uid());

        // 'btn_enter_customer' 버튼 클릭 이벤트 처리
        holder.btn_enter_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setIcon(R.drawable.ic_warning);
                ad.setTitle("< 대기자 >");
                ad.setMessage("대기자 입장하시겠습니까?");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // position 변수를 사용하여 현재 아이템의 위치 확인 가능
                        int clickedPosition = holder.getAdapterPosition();

                        // Firestore 문서에 대한 참조를 얻습니다.
                        DocumentReference docRef = mStore.collection(FirebaseID.rest_waiting_list).document(RES_ID);

                        // Firestore에서 데이터를 삭제하기 위해 arrayRemove 업데이트 사용
                        // Waiting 객체 대신 맵을 사용하여 해당 위치의 데이터를 삭제
                        Map<String, Object> itemToRemove = new HashMap<>();
                        itemToRemove.put("restaurantOpenData_id", RES_ID);
                        itemToRemove.put("uid", cust_uid);
                        itemToRemove.put("wait_name", Waitinglist.get(clickedPosition).getWaiting_name());
                        itemToRemove.put("wait_tel", Waitinglist.get(clickedPosition).getWaiting_tel());
                        itemToRemove.put("wait_num", Waitinglist.get(clickedPosition).getWaiting_num());

                        docRef.update("list", FieldValue.arrayRemove(itemToRemove))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
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

                                                        mStore.collection(FirebaseID.rest_waiting_list).document(RES_ID)
                                                                .set(wait_queueMap, SetOptions.merge());
                                                        //Toast.makeText(context.getApplicationContext(), "변동rest_FireStore 반영 완료", Toast.LENGTH_SHORT).show();
                                                        Map<String, Object> cust_itemToRemove = new HashMap<>();
                                                        cust_itemToRemove.put("opendata_id", RES_ID);
                                                        mStore.collection(FirebaseID.cust_waiting_list)
                                                                .document(cust_uid).update("my_waiting_list", FieldValue.arrayRemove(cust_itemToRemove))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //Toast.makeText(context.getApplicationContext(), "cust_FireStore 반영 완료", Toast.LENGTH_SHORT).show();

                                                                        // 업데이트에 성공하면, RecyclerView에서도 해당 아이템을 제거
                                                                        Waitinglist.remove(clickedPosition);
                                                                        notifyItemRemoved(clickedPosition);
                                                                        Toast.makeText(context.getApplicationContext(), "대기 입장 완료", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                                        // 업데이트에 성공하면, RecyclerView에서도 해당 아이템을 제거
                                        Waitinglist.remove(clickedPosition);
                                        notifyItemRemoved(clickedPosition);
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
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context.getApplicationContext(), "대기 입장 취소했습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });


        // 'btn_delete_customer' 버튼 클릭 이벤트 처리
        holder.btn_delete_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
                ad1.setIcon(R.drawable.ic_warning);
                ad1.setTitle("< 확인 >");
                ad1.setMessage("대기자를 삭제하시겠습니까?");

                ad1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // position 변수를 사용하여 현재 아이템의 위치 확인 가능
                        int clickedPosition = holder.getAdapterPosition();

                        // Firestore 문서에 대한 참조를 얻습니다.
                        DocumentReference docRef = mStore.collection(FirebaseID.rest_waiting_list).document(RES_ID);

                        // Firestore에서 데이터를 삭제하기 위해 arrayRemove 업데이트 사용
                        // Waiting 객체 대신 맵을 사용하여 해당 위치의 데이터를 삭제
                        Map<String, Object> itemToRemove = new HashMap<>();
                        itemToRemove.put("restaurantOpenData_id", RES_ID);
                        itemToRemove.put("uid", cust_uid);
                        itemToRemove.put("wait_name", Waitinglist.get(clickedPosition).getWaiting_name());
                        itemToRemove.put("wait_tel", Waitinglist.get(clickedPosition).getWaiting_tel());
                        itemToRemove.put("wait_num", Waitinglist.get(clickedPosition).getWaiting_num());

                        docRef.update("list", FieldValue.arrayRemove(itemToRemove))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
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

                                                        mStore.collection(FirebaseID.rest_waiting_list).document(RES_ID)
                                                                .set(wait_queueMap, SetOptions.merge());
                                                        Toast.makeText(context.getApplicationContext(), "rest_FireStore 반영 완료", Toast.LENGTH_SHORT).show();

                                                        Map<String, Object> cust_itemToRemove = new HashMap<>();
                                                        cust_itemToRemove.put("opendata_id", RES_ID);
                                                        mStore.collection(FirebaseID.cust_waiting_list)
                                                                .document(cust_uid).update("my_waiting_list", FieldValue.arrayRemove(cust_itemToRemove))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(context.getApplicationContext(), "cust_FireStore 반영 완료", Toast.LENGTH_SHORT).show();

                                                                        // 업데이트에 성공하면, RecyclerView에서도 해당 아이템을 제거
                                                                        Waitinglist.remove(clickedPosition);
                                                                        notifyItemRemoved(clickedPosition);
                                                                        Toast.makeText(context.getApplicationContext(), "대기 삭제ㅕ 완료", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
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
        return (Waitinglist != null ? Waitinglist.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView customer_name;
        public TextView customer_num_of_group;
        public TextView customer_tel_number;
        public Button btn_enter_customer;
        public Button btn_delete_customer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_num_of_group = itemView.findViewById(R.id.customer_num_of_group);
            customer_tel_number = itemView.findViewById(R.id.customer_tel_number);
            btn_enter_customer = itemView.findViewById(R.id.btn_enter_customer);
            btn_delete_customer = itemView.findViewById(R.id.btn_delete_customer);
        }
    }
}