package com.mattyang.demos.networkcirclemap;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class MyLayoutManager extends RecyclerView.LayoutManager {
    private Context mContext;
    private ArrayList<MyItem> types;
    private int parentCount = 0;
    int centerX = 0;
    int centerY = 0;
    int mainCircleRadius = 0;
    int subCircleRadius = 0;


    public MyLayoutManager(Context context, ArrayList<MyItem> value) {
        this.mContext = context;
        this.types = value;
        dataResort(types);
        centerX = (getDisplayMetrics().widthPixels - 50) / 2;
        centerY = getDisplayMetrics().heightPixels / 2;
        mainCircleRadius = dp2px(110);
        subCircleRadius = dp2px(40);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        float[] main = generatePoints(centerX,centerY,mainCircleRadius,parentCount);
        int mainNo = 0;
        int childNo = 0;
        int parentIndex = -1;
        for(int i = 0; i <  getItemCount(); i++){
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view,0,0);
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            Rect rect = new Rect();
            calculateItemDecorationsForChild(view,rect);
            switch (types.get(i).category){
                case "P":
                    layoutDecorated(view,(int)(main[mainNo * 2]- width/2),
                            (int)(main[mainNo * 2 +1] - height/2),
                            (int)(main[mainNo * 2]+width/2),
                            (int)(main[mainNo * 2 + 1]+height/2));
                    mainNo++;
                    break;
                case "C":
                    for(int j = i - 1; j >= 0; j--){
                        if(types.get(j).category.equals("P")){
                            if(parentIndex != j){
                                childNo = 0;
                            }
                            parentIndex = j;
                            int childTotal = types.get(j).getChildCount();
                            float[] child = generateSubPoints((int)(main[j * 2]),(int)(main[j * 2 + 1]),subCircleRadius,childTotal,centerX,centerY);
                            if(child.length != 0) {
                                layoutDecorated(view, (int) (child[childNo * 2] - width / 2),
                                        (int) (child[childNo * 2 + 1] - height / 2),
                                        (int) (child[childNo * 2] + width / 2),
                                        (int) (child[childNo * 2 + 1] + height / 2));
                                childNo++;
                            }
                            break;
                        }
                    }
                    break;
            }
        }
    }

    int index = 0;
    boolean isFirst = true;
    private void dataResort(ArrayList<MyItem> types){
        for(MyItem item : types){
            if(item.category.equals("P"))
                parentCount++;
        }
        if(types.get(types.size() - 1).equals("P")){
            for(int j = 0 ; j < types.size(); j++){
                if(isFirst){
                    index = j;
                    continue;
                }
                int total = j - index - 1;
                types.get(index).setChildCount(total);
                index = j;
            }
            types.get(index).setChildCount(0);
        }else if(types.get(types.size() - 1).equals("C")){
            for(int i = 0; i < types.size(); i++){
                if(types.get(i).category.equals("P")){
                    if(isFirst){
                        index = i;
                        continue;
                    }
                    int total = i - index - 1;
                    types.get(index).setChildCount(total);
                    index = i;
                }
            }
            if(index < types.size() - 1){
                types.get(index).setChildCount(types.size() - 1 - index);
            }
        }
    }

    private float[] generatePoints(int x, int y, int radius,int count){
        float[] points = new float[count * 2];
        for(int j = 0; j < count; j++){
            double locationX = radius * (Math.cos(Math.PI * 2 / count * j - Math.PI / 2)) + x;
            double locationY = radius * (Math.sin(Math.PI * 2 / count * j - Math.PI / 2)) + y;
            points[j * 2] = (float)locationX;
            points[j * 2 + 1] = (float) locationY;
        }
        return points;
    }

    private float[] generateSubPoints(int x, int y, int radius, int count, int centerX,int centerY){
        float[] points = new float[count * 2];
        double angle = getAngle(x,y,centerX,centerY);
            for(int j = 0; j < count; j++){
                double locationX = radius * (Math.cos(Math.PI * 2 / count * j - Math.PI / 2 + angle)) + x;
                double locationY = radius * (Math.sin(Math.PI * 2 / count * j - Math.PI / 2 + angle)) + y;
                points[j * 2] = (float)locationX;
                points[j * 2 + 1] = (float) locationY;
        }
        return points;
    }

    private double getAngle(int pointX, int pointY, int centerX, int centerY){
        double dx = pointX - centerX;
        double dy = centerY - pointY;
        double result = Math.toDegrees(Math.atan2(dy,dx));
        if(result < 0){
            result = 360d + result;
        }
        result = ((180 - result - 90) / 360) * Math.PI * 2;
        return result;
    }

    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private int dp2px(int dp){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int px = (int)(dp * metrics.density);
        return px;
    }
}
