package com.mattyang.demos.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mattyang.demos.SQLite.FeedReaderContract;
import com.mattyang.demos.SQLite.FeedReaderDbHelper;

public class myContentProvider extends ContentProvider {
    public static String AUTHORITY = "com.mattyang.demos";
    public static final int TITLE = 1;
    private Context mContext;
    FeedReaderDbHelper mDbHelper = null;
    SQLiteDatabase db;
    private static final UriMatcher mMatcher;
    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(AUTHORITY,"title",TITLE);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new FeedReaderDbHelper(mContext);
        db = mDbHelper.getWritableDatabase();
        db.execSQL("delete from title");
        db.execSQL("insert into title values(1,'Carson');");
        db.execSQL("insert into title values(2,'Kobe');");
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String table = getTableName(uri);
        return db.query(table,projection,selection,selectionArgs,null,null,sortOrder);
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String table = getTableName(uri);
        db.insert(table,null,contentValues);
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    private String getTableName(Uri uri){
        String tableName = null;
        switch (mMatcher.match(uri)){
            case TITLE:
                tableName = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE;
                break;
        }
        return tableName;
    }
}
