package com.chenji.lock.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;

/**
 * Created by 志瑞 on 2015/12/22.
 */
public class CreateSql extends SQLiteOpenHelper {

    public CreateSql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CreateSql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public CreateSql() {

        super(MyApplication.getContext(), SqlInfo.DATABASE_NAME, null, SqlInfo.DATABASE_VERSION);
        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        // CursorFactory设置为null,使用系统默认的工厂类
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 调用时间：数据库第一次创建时onCreate()方法会被调用
        // onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
        // 这个方法中主要完成创建数据库后对数据库的操作
        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("create table " + SqlInfo.TABLE_Info + " (");
        //app信息
        sBuffer.append(SqlInfo.APP_NAME + " varchar(30) ,");
        sBuffer.append(SqlInfo.PACKAGE + " varchar(40) not null primary key ,");
        sBuffer.append(SqlInfo.ICON + " blob,");
        sBuffer.append(SqlInfo.APP_TYPE + " integer not null default 1,");
        sBuffer.append(SqlInfo.INSTALL + " integer not null default 1 ,");

        sBuffer.append(SqlInfo.LOCK_WEEK + " integer not null default 0,");
        sBuffer.append(SqlInfo.LOCK_MONDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_TUESDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_WEDNESDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_THURSDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_FRIDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_SATURDAY + " integer not null default 86400,");
        sBuffer.append(SqlInfo.LOCK_SUNDAY+ " integer not null default 86400,");

        sBuffer.append(SqlInfo.LOCK_OPEN + " integer not null default 0)");

        db.execSQL(sBuffer.toString());

        MyLog.i_chenji_log("table info create ok");





        StringBuffer sBufferLock = new StringBuffer();
        sBufferLock.append("create table " + SqlInfo.TABLE_Lock + " (");
        sBufferLock.append(SqlInfo.PACKAGE+ " varchar(40) NOT NULL ,");

        sBufferLock.append(SqlInfo.LOCK_ORDER + " integer not null primary key ,");

        sBufferLock.append(SqlInfo.LOCK_TYPE + " integer not null,");
        sBufferLock.append(SqlInfo.LOCK_OPEN + " integer not null default 1,");

        sBufferLock.append(SqlInfo.LOCK_WEEK + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_MONDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_TUESDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_WEDNESDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_THURSDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_FRIDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_SATURDAY + " integer not null default 0,");
        sBufferLock.append(SqlInfo.LOCK_SUNDAY+ " integer not null default 0,");

        sBufferLock.append(SqlInfo.LOCK_START_TIME + " integer not null,");
        sBufferLock.append(SqlInfo.LOCK_FINISH_TIME + " integer not null)");

        db.execSQL(sBufferLock.toString());

        MyLog.i_chenji_log("table lock create ok");



       /* StringBuffer sBufferImage = new StringBuffer();

        sBufferImage.append("create table " + SqlInfo.TABLE_IMAGE + " (");
        //app信息
        sBufferImage.append(SqlInfo.IMAGE_IMAGE + " blob not null ,");
        sBufferImage.append(SqlInfo.IMAGE_ORDER + " integer not null primary key ,");
        sBufferImage.append(SqlInfo.IMAGE_SELECTED + " integer not null default 0 )");

        db.execSQL(sBufferImage.toString());

        MyLog.i_chenji_log("table image create ok");*/

        /*db.execSQL("create table " + SqlInfo.TABLE_FORBIDDEN + " (" +
                SqlInfo.FORBIDDEN + " varchar(30) )");
        db.execSQL("create table " + SqlInfo.TABLE_ONLY_USE + " (" +
                SqlInfo.ONLY_USE + " varchar(30) )");*/



        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
