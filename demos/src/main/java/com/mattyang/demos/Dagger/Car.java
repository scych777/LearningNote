package com.mattyang.demos.Dagger;

import android.util.Log;

import javax.inject.Inject;

public class Car {
    @Inject
    public Car(){}

    public void show(){
        Log.d("Matt","This is a car");
    }
}
