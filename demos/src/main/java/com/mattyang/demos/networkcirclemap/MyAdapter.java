package com.mattyang.demos.networkcirclemap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattyang.demos.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    public ArrayList<MyItem> mDataset;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public MyViewHolder(@NonNull View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_text);
        }
    }

    public MyAdapter(ArrayList<MyItem> mDataset) {
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.network_recycler_item,viewGroup,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.mTextView.setText(mDataset.get(i).category);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<MyItem> getAdapterData(){
        return mDataset;
    }
}
