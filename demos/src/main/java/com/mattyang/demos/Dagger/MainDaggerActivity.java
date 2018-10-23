package com.mattyang.demos.Dagger;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mattyang.demos.R;

public class MainDaggerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rxjava_activity);
        Man man = new Man();
        DaggerManComponent.create().injectMan(man);
        man.car.show();
    }
}
