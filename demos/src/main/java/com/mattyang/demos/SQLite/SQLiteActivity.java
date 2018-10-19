package com.mattyang.demos.SQLite;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattyang.demos.R;

public class SQLiteActivity extends Activity {
    EditText input1,input2;
    Button save,read,delete,update;
    TextView display;
    FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sqliteactivity);
        mDbHelper = new FeedReaderDbHelper(this);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        input1 = (EditText) findViewById(R.id.input_one);
        input2 = (EditText) findViewById(R.id.input_two);
        display = (TextView) findViewById(R.id.display);
        read = (Button) findViewById(R.id.read);
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);
        update = (Button) findViewById(R.id.update);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,input1.getText().toString().trim());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,input2.getText().toString().trim());
                db.insert(FeedReaderContract.FeedEntry.TABLE_NAME,null,values);
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                Cursor cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME,null,null,null,null,null,null);
                String result=  "";
                while (cursor.moveToNext()){
                    result += cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)) + "\n";
                }
                cursor.close();
                display.setText(result);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(FeedReaderContract.FeedEntry.TABLE_NAME,null,null);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String title = "MyNewTitle";
                String[] args = {"mytitle3"};
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,title);
                db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE +" LIKE?",args);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
