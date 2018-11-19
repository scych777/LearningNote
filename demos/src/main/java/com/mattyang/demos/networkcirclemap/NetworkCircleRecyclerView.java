package com.mattyang.demos.networkcirclemap;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;

public class NetworkCircleRecyclerView extends RecyclerView {

    public NetworkCircleRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NetworkCircleRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        ArrayList<MyItem> data = ((MyAdapter)getAdapter()).getAdapterData();
        if(data != null){

        }
    }
}
