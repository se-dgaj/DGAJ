package com.example.dgaj;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myLoveAdapter extends RecyclerView.Adapter<myLoveAdapter.MyLoveViewHolder> {

    private List<myLove> myLovelist;
    private Context context;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String uid;
    private String opendata_id;
    private String address_gu;

    public myLoveAdapter(List<myLove> myLovelist, Context context, String uid, String opendata_id, String address_gu) {
        this.myLovelist = myLovelist;
        this.context = context;
        this.uid = uid;
        this.opendata_id = opendata_id;
        this.address_gu = address_gu;
    }

    @NonNull
    @Override
    public MyLoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_love_list_item, parent, false);
        MyLoveViewHolder holder = new MyLoveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyLoveViewHolder holder, int position) {
        holder.myLoveResNameTextView.setText(myLovelist.get(position).getName());
        holder.myLoveResAddrTextView.setText(myLovelist.get(position).getTel());
        holder.myLoveResTimeTextView.setText(myLovelist.get(position).getTime());
        String opendata_id = myLovelist.get(position).getOpendata_id();
        String address_gu = myLovelist.get(position).getAddress_gu();

        final DocumentReference restLoveListRef = mStore.collection(FirebaseID.rest_love_list).document(opendata_id);

        holder.btnDeleteLove.setOnClickListener(view -> {
            // 클릭 이벤트 발생 시 리스너를 통해 데이터 전달
            int clickedPosition = holder.getAdapterPosition();
            // Firestore 문서에 대한 참조를 얻습니다.
            DocumentReference docRef = mStore.collection(FirebaseID.cust_love_list).document(uid);
            // Firestore에서 데이터를 삭제하기 위해 arrayRemove 업데이트 사용
            // Waiting 객체 대신 맵을 사용하여 해당 위치의 데이터를 삭제
            Map<String, Object> itemToRemove = new HashMap<>();
            itemToRemove.put("opendata_id", opendata_id);
            itemToRemove.put("address_gu", address_gu);

            docRef.update("my_love_list", FieldValue.arrayRemove(itemToRemove))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mStore.collection(FirebaseID.cust_love_list).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            restLoveListRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().exists()) {

                                                        } else {
                                                            // 문서가 이미 있는 경우 totallove를 1씩 감소
                                                            restLoveListRef.update("totallove", FieldValue.increment(-1));
                                                        }
                                                    } else {
                                                    }
                                                }
                                            });

                                            Toast.makeText(context.getApplicationContext(), "변동 반영 완료", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }
                            });
                            // 업데이트에 성공하면, RecyclerView에서도 해당 아이템을 제거
                            myLovelist.remove(clickedPosition);
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
        });
    }

    @Override
    public int getItemCount() {
        return myLovelist.size();
    }

    public static class MyLoveViewHolder extends RecyclerView.ViewHolder {
        TextView myLoveResNameTextView;
        TextView myLoveResAddrTextView;
        TextView myLoveResTimeTextView;
        Button btnDeleteLove;

        public MyLoveViewHolder(@NonNull View itemView) {
            super(itemView);
            myLoveResNameTextView = itemView.findViewById(R.id.myLove_ResNameTextView);
            myLoveResAddrTextView = itemView.findViewById(R.id.myLove_ResAddrTextView);
            myLoveResTimeTextView = itemView.findViewById(R.id.myLove_ResTimeTextView);
            btnDeleteLove = itemView.findViewById(R.id.btn_delete_love);
        }
    }

}