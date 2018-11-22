package com.mattyang.demos.networkcirclemap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.mattyang.demos.R;

import java.util.ArrayList;

public class TestActivity extends Activity {
//    NetworkCircleMapView map;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private MyLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_circlemap_layout);
//        map = (NetworkCircleMapView) findViewById(R.id.MapView);
//        map.setNetWorkOnClickListener(new NetworkCircleMapView.OnClickListener() {
//            @Override
//            public void onClick(NetworkItem item) {
//                Toast.makeText(TestActivity.this,String.valueOf(item.getIndex()),Toast.LENGTH_SHORT).show();
//            }
//        });
//        NetworkItem item1 = new NetworkItem("2 Floor","00:11:22:33:44:A1","");
//        item1.setIndex(999);
//        item1.setPhoneNickName("Chelsea Iphone");
//        NetworkItem item2 = new NetworkItem("3 Floor","00:11:22:33:44:A2","00:11:22:33:44:A1");
//        item2.setIndex(1);
//        NetworkItem item3 = new NetworkItem("4 Floor","00:11:22:33:44:A3","00:11:22:33:44:A1");
//        item3.setIndex(2);
//        NetworkItem item4 = new NetworkItem("5 Floor","00:11:22:33:44:A4","00:11:22:33:44:A1");
//        item4.setIndex(3);
//        NetworkItem item5 = new NetworkItem("6 Floor","00:11:22:33:44:A5","00:11:22:33:44:A3");
//        item5.setIndex(101);
//        NetworkItem item6 = new NetworkItem("7 Floor","00:11:22:33:44:A6","00:11:22:33:44:A4");
//        item6.setIndex(102);
//        NetworkItem item7 = new NetworkItem("8 Floor","00:11:22:33:44:A7","00:11:22:33:44:A5");
//        NetworkItem item8 = new NetworkItem("9 Floor","00:11:22:33:44:A8","00:11:22:33:44:A6");
//        NetworkItem item9 = new NetworkItem("10 Floor","00:11:22:33:44:B0","00:11:22:33:44:A3");
//        item9.setIndex(103);
//        NetworkItem item10 = new NetworkItem("11 Floor","00:11:22:33:44:B1","00:11:22:33:44:A3");
//        item10.setIndex(104);
//        NetworkItem item11 = new NetworkItem("12 Floor","00:11:22:33:44:B2","00:11:22:33:44:A2");
//        item11.setIndex(105);
//        NetworkItem item12 = new NetworkItem("13 Floor","00:11:22:33:44:B3","00:11:22:33:44:A2");
//        item12.setIndex(106);
//        NetworkItem item13 = new NetworkItem("14 Floor","00:11:22:33:44:B4","00:11:22:33:44:A3");
//        item13.setIndex(107);
//        ArrayList<NetworkItem> list1 = new ArrayList<>();
//        list1.add(item1);
//        list1.add(item2);
//        list1.add(item3);
//        list1.add(item4);
//        list1.add(item5);
//        list1.add(item6);
//        list1.add(item7);
//        list1.add(item8);
//        list1.add(item9);
//        list1.add(item10);
//        list1.add(item11);
//        list1.add(item12);
//        list1.add(item13);
//        map.setDataList(list1);
//        map.setCenterItemBitmap(R.drawable.router);

        MyItem item1 = new MyItem();
        item1.setCategory("P");
        MyItem item2 = new MyItem();
        item2.setCategory("C");
        MyItem item3 = new MyItem();
        item3.setCategory("P");
        MyItem item4 = new MyItem();
        item4.setCategory("C");
        MyItem item5 = new MyItem();
        item5.setCategory("P");
        MyItem item6 = new MyItem();
        item6.setCategory("C");
        MyItem item7 = new MyItem();
        item7.setCategory("P");
        MyItem item8 = new MyItem();
        item8.setCategory("C");
        MyItem item9 = new MyItem();
        item9.setCategory("C");
        ArrayList<MyItem> data = new ArrayList<>();
        data.add(item1);
        data.add(item2);
        data.add(item3);
        data.add(item4);
        data.add(item5);
        data.add(item6);
        data.add(item7);
        data.add(item8);
        data.add(item9);
        data.add(item9);
        data.add(item9);
        data.add(item7);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new MyLayoutManager(this,data);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }
}
