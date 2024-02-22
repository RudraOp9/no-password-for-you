package com.leo.nopasswordforyou.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import com.leo.nopasswordforyou.R;


import java.util.ArrayList;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.MyViewHolder> {
    private  ArrayList<PassAdapterData> listData;
    private String uid;
    FirebaseFirestore db;
    public ItemClickListner clickListner;

    public void setClickListener(ItemClickListner clickListener) {
        this.clickListner = clickListener;
    }


    public PassAdapter(ArrayList<PassAdapterData> listData,String uid,FirebaseFirestore db) {
        this.listData = listData;
        this.db = db;
        this.uid = uid;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.custom_pass_list,parent, false);
        return new MyViewHolder(listItem);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     //   final PassAdapterData myListData = listData.get(position);
        holder.passTitle.setText(listData.get(position).getTitle());

        holder.passDesc.setText(listData.get(position).getDescription());
    }





    @Override
    public int getItemCount() {
        return listData.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView passTitle;
        public TextView passDesc;
        public MaterialCardView passParent;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.passTitle = itemView.findViewById(R.id.passTitle);
            this.passParent = itemView.findViewById(R.id.passParent);
            this.passDesc = itemView.findViewById(R.id.passDesc);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (clickListner != null){
                clickListner.onClick(v, listData.get(getAdapterPosition()).getId());
            }
        }
    }

}
