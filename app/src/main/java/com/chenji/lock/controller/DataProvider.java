package com.chenji.lock.controller;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.model.DataUri;

public class DataProvider extends ContentProvider {

    public static final int INTERCEPT=11;
    public static final int LOCK=21;
    public static final int INFO=22;
    public static final int RUNNING=31;
    public static final int OPEN_LOCK=41;
    public static final int LAUNCHER=51;
    public static final int BOOT_COMPLETED=61;

    public DataProvider() {

    }
    @Override
    public boolean onCreate() {
        MyLog.w_chenji_log("DataProvider  create");
        return true;
    }


    public static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(DataUri.CONTENT,DataUri.INTERCEPT ,INTERCEPT);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.LOCK ,LOCK);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.INFO ,INFO);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.RUNNING ,RUNNING);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.OPEN_LOCK ,OPEN_LOCK);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.LAUNCHER ,LAUNCHER);
        uriMatcher.addURI(DataUri.CONTENT,DataUri.BOOT_COMPLETED ,BOOT_COMPLETED);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)){
            case LOCK:{
                SqlOperation  sqlOperation=new SqlOperation();
               return sqlOperation.queryLock(projection,selection,selectionArgs,sortOrder);
            }
            case INFO:{
                SqlOperation  sqlOperation=new SqlOperation();
               return sqlOperation.queryInfo(projection,selection,selectionArgs,sortOrder);
            }
            default:{
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case LOCK: {
                SqlOperation  sqlOperation=new SqlOperation();
                sqlOperation.deleteLock(selection,selectionArgs);
                return 0;
            }
            case RUNNING:{
                return SqlOperation.Running;
            }
            case OPEN_LOCK:{
                SharedPreferences mySharedPreferences=MyApplication.getContext().
                        getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
               if (mySharedPreferences.getBoolean(MyApplication.OPEN_LOCK, true)){
                   return 1;
               }else {
                   return 0;
               }
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
          case LOCK: {
              SqlOperation  sqlOperation=new SqlOperation();
              sqlOperation.insertLock(values);
              return null;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case INTERCEPT:{
                SqlOperation.Judge_Interval=values.getAsFloat(MyApplication.JUDGE_INTERVAL);
                SqlOperation.Intercept_Interval=values.getAsInteger(MyApplication.INTERCEPT_INTERVAL);
                SharedPreferences mySharedPreferences=MyApplication.getContext().
                        getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=mySharedPreferences.edit();
                editor.putFloat(MyApplication.JUDGE_INTERVAL,SqlOperation.Judge_Interval);
                editor.putInt(MyApplication.INTERCEPT_INTERVAL, SqlOperation.Intercept_Interval);
                editor.apply();
                return 1;
            }
            case INFO:{
                SqlOperation sqlOperation=new SqlOperation();
                sqlOperation.updateLockOverTime(values,selection,selectionArgs);
                return 1;
            }
            case LOCK:{
                SqlOperation sqlOperation=new SqlOperation();
                sqlOperation.updateLock(values,selection,selectionArgs);
                return 1;
            }
            case OPEN_LOCK:{
                SqlOperation.Open_Lock=values.getAsBoolean(DataUri.OPEN_LOCK);
                SharedPreferences mySharedPreferences = MyApplication.getContext().
                        getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                myEditor.putBoolean(MyApplication.OPEN_LOCK,SqlOperation.Open_Lock);
                myEditor.apply();
                return 1;
            }
            case LAUNCHER:{
                SqlOperation.Launcher=values.getAsString(DataUri.LAUNCHER);
                SharedPreferences mySharedPreferences = MyApplication.getContext().
                        getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                myEditor.putString(MyApplication.LAUNCHER, SqlOperation.Launcher);
                myEditor.apply();
                return 1;
            }
            case BOOT_COMPLETED:{
                SharedPreferences mySharedPreferences = MyApplication.getContext().
                        getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                myEditor.putBoolean(MyApplication.SET_BOOT_COMPLETED,values.getAsBoolean(MyApplication.SET_BOOT_COMPLETED));
                myEditor.apply();
                return 1;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

}
