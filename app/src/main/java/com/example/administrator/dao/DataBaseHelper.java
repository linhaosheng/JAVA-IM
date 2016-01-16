package com.example.administrator.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/9/17.
 */
public class DataBaseHelper extends SDCardSQLiteOpenHelper{
    public DataBaseHelper(Context context,String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE [im_msg_his]([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[content] NVARCHAR,[msg_from] NVARCHAR,[msg_time] TEXT,[msg_type] INTEGER);");
         db.execSQL("CREATE TABLE [im_notice]([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[type] INTEGER,[title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, [notice_time] TEXT, [status] INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
