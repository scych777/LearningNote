package com.mattyang.demos.networkcirclemap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mattyang.demos.R;

public class TestActivity extends Activity {
    NetworkCircleMapView map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_circlemap_layout);
        map = (NetworkCircleMapView) findViewById(R.id.MapView);
        map.setNetWorkOnClickListener(new NetworkCircleMapView.OnClickListener() {
            @Override
            public void onClick(NetworkItem item) {
                Toast.makeText(TestActivity.this,String.valueOf(item.getIndex()),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
