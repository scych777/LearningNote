package com.mattyang.demos.RxJava;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxjavaActivity extends Activity {
    private static final String TAG = "Matt";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = Uri.parse("content://con.mattyang.demos/title");
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscrible");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
            }
        };
        ContentValues values = new ContentValues();
        values.put("id",3);
        values.put("title","Iverson");
        ContentResolver resolver = getContentResolver();
        resolver.insert(uri,values);
        Observable.just("hello","world").subscribeOn(Schedulers.newThread()).subscribe(observer);

    }
}
