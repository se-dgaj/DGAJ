package com.example.dgaj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.CustomViewHolder> {

    private List<Donation> donationList;

    public DonationAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        holder.tv_article.setText(donation.getArticle());
        holder.tv_centerName.setText(donation.getCenterName());
        holder.tv_cnt.setText(donation.getCnt());
        holder.tv_donorName.setText(donation.getDonorName());
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_article;
        TextView tv_centerName;
        TextView tv_cnt;
        TextView tv_donorName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_article = itemView.findViewById(R.id.articleTextView);
            tv_centerName = itemView.findViewById(R.id.centerNameTextView);
            tv_cnt = itemView.findViewById(R.id.cntTextView);
            tv_donorName = itemView.findViewById(R.id.donorNameTextView);
        }
    }
}