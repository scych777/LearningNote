package com.mattyang.demos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewTest extends AppCompatActivity {
    RecyclerView viewOne;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recyclerview_test);
        viewOne = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mAdapter = new MynewAdapter(getData());
        viewOne.setLayoutManager(manager);
        viewOne.setAdapter(mAdapter);

    }

    private ArrayList<String> getData(){
        ArrayList<String> data = new ArrayList<>();
        String temp = "item";
        for(int i = 0; i < 20 ; i++){
            data.add(i+temp);
        }
        return data;
    }

    class MynewAdapter extends RecyclerView.Adapter<MynewAdapter.ViewHolder>{
        private ArrayList<String> mData;

        public MynewAdapter(ArrayList<String> mData) {
            this.mData = mData;
        }

        public void updatData(ArrayList<String> data){
            this.mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MynewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_rv_item,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.mTv.setText(mData.get(i));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0: mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView mTv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.mTv = (TextView) itemView.findViewById(R.id.item_tv);
            }
        }
    }
}
