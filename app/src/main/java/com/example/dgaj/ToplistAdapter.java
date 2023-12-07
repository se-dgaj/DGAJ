package com.example.dgaj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ToplistAdapter extends RecyclerView.Adapter<ToplistAdapter.ViewHolder> {

    private ArrayList<Toplist> toplist;
    private List<Restaurant> restaurantList = new ArrayList<>();

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Context context;
    private String uid;
    private RestaurantAdapter.OnItemClickListener listener; // 추가: 클릭 이벤트 리스너


    public void setOnItemClickListener(RestaurantAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public ToplistAdapter(ArrayList<Toplist> toplist, List<Restaurant> restaurantList, Context context, String uid) {
        this.toplist = toplist;
        this.toplist = toplist;
        this.restaurantList = restaurantList;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ToplistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        ToplistAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ToplistAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.cuisineTextView.setText(restaurant.getCuisine());
        holder.phoneNumberTextView.setText(restaurant.getPhoneNumber());
        holder.addressTextView.setText(restaurant.getAddress());

        // 추가: 항목을 클릭했을 때 이벤트 처리
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView cuisineTextView;
        public TextView phoneNumberTextView;
        public TextView addressTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            cuisineTextView = itemView.findViewById(R.id.cuisineTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);


        }
    }


    // 추가: 클릭 이벤트 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
