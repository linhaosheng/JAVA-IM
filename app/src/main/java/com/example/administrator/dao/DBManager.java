package com.example.administrator.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/9/15.
 * 该类用来提供数据库的打开和关闭  以及获得DatabaseHelper帮助类操作
 */
public class DBManager {
    private int VERSION=1;
    private String DATABASE_NAME=null;
    private Context context=null;
    private static DBManager dbManager=null;

    public DBManager(Context context){
        this.context=context;
    }
    public static DBManager getInstance(Context context,String databaseNmae){
        if(dbManager==null){
            dbManager=new DBManager(context);
        }
        dbManager.DATABASE_NAME=databaseNmae;
        return dbManager;
    }
    /**
     * 关闭数据库   当事务成功或者一次性操作数据完毕时再关闭
     */
    public void closeDatabase(SQLiteDatabase sqLiteDatabase,Cursor cursor){
        if(sqLiteDatabase!=null){
            sqLiteDatabase.close();
        }
        if (cursor!=null){
            cursor.close();
        }
    }
    /**
     * 打开数据库 注：SQLiteDatabase资源一旦被关闭，重启时该底层会重新产生一个SQLiteDatabase
     */
    public SQLiteDatabase openDatabase(){
        return getDatabaseHelp().getWritableDatabase();
    }
    /**
     * 获取DatabaseHelp
     */
    public DataBaseHelper getDatabaseHelp(){
        return new DataBaseHelper(context,this.DATABASE_NAME,null,this.VERSION);
    }
}
