package com.leo.nopasswordforyou.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.nopasswordforyou.R;

import java.util.ArrayList;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.MyViewHolder> {
    private ArrayList<PassAdapterData> listData;

    public PassAdapter(ArrayList<PassAdapterData> listData) {
        this.listData = listData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.custom_pass_list,parent, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final PassAdapterData myListData = listData.get(position);
        holder.passTitle.setText(listData.get(position).getTitle());
        holder.passDesc.setText(listData.get(position).getDescription());
    }




    @Override
    public int getItemCount() {
        return listData.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView passTitle;
        public TextView passDesc;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.passTitle = itemView.findViewById(R.id.passTitle);
            this.passDesc = itemView.findViewById(R.id.passDesc);
        }



    }

}
