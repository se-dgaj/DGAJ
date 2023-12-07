package com.example.dgaj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DonatingAdapter extends RecyclerView.Adapter<DonatingAdapter.CustomViewHolder> {

    private ArrayList<Donating> donatingList;
    private Context context;

    public DonatingAdapter(ArrayList<Donating> arrayList, Context context) {
        this.donatingList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_to_donate, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {
        Donating donating = donatingList.get(position);

        viewHolder.tv_name.setText(donating.getName());
        viewHolder.tv_address.setText(donating.getAddress());
        viewHolder.tv_tel.setText(donating.getTel());

        // Set image using the resource ID from Donating class
        viewHolder.iv_profile.setImageResource(donating.getImgResourceId());

        viewHolder.btn_donate_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = donating.getName();

                Context context = v.getContext();
                Toast.makeText(context, name + "로 기부~", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, Owner_donating_Info.class);
                intent.putExtra("name", name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (donatingList != null ? donatingList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_name;
        TextView tv_address;
        TextView tv_tel;
        Button btn_donate_here;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.profile);
            this.tv_name = itemView.findViewById(R.id.donating_name);
            this.tv_address = itemView.findViewById(R.id.donating_address);
            this.tv_tel = itemView.findViewById(R.id.donating_tel);
            this.btn_donate_here = itemView.findViewById(R.id.btn_donate_here);
        }
    }
}