package com.example.administrator.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.activity.R;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/9/15.
 */
public abstract class SDCardSQLiteOpenHelper {
    private static final String TAG=SDCardSQLiteOpenHelper.class.getSimpleName();
    private  Context context;
    private  String name;
    private  SQLiteDatabase.CursorFactory factory;
    private int newVersion;

     private SQLiteDatabase database=null;
    private boolean isInitializing=false;

    public SDCardSQLiteOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Version must be >=1,was" + version);
        }
        this.context = context;
        this.name = name;
        this.newVersion = version;
        this.factory = factory;
    }

    /**
     * 获取一个getWritableDatabase
     * @return
     */
        public synchronized SQLiteDatabase getWritableDatabase(){
        if(database!=null &&database.isOpen() && !database.isReadOnly()){
            return database;
        }
            if(isInitializing){
                throw new IllegalStateException("getWritableDatabase called recursively");
    }
            boolean succes =false;
            SQLiteDatabase db=null;
            try{
                isInitializing=true;
                if(name==null){
                    db=SQLiteDatabase.create(null);
                }else {
                    String path=getDatabasePath(name).getPath();
                    db=SQLiteDatabase.openOrCreateDatabase(path,factory);
                }
                int version=db.getVersion();
                if(version!=newVersion){
                    db.beginTransaction();
                    try{
                        if(version==0){
                            onCreate(db);
                        }else{
                            onUpgrade(db,version,newVersion);
                        }
                        db.setVersion(newVersion);
                        db.setTransactionSuccessful();
                    }finally {
                        db.endTransaction();
                    }
                }
                onOpen(db);
                succes=true;
                return db;
            }catch (Exception e){
                 e.printStackTrace();
            }finally {
                isInitializing=false;
                if(succes){
                    if(database!=null){
                        try{
                            database.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    database=db;
                }else {
                    if(db!=null)
                     db.close();
                }
            }
            return db;
    }

    /**
     * 获得一个Readable
     * @param
     * @return
     */
    public synchronized SQLiteDatabase getReadableDatabase(){
        if(database!=null && database.isOpen()){
            return database;
        }
        if(isInitializing){
            throw new IllegalStateException("getReadableDatabase called recursively");
        }
        try{
            return getWritableDatabase();
        }catch (SQLiteException e){
            if(name==null)
                throw e;
            Log.e(TAG, "Couldn't open " + name
                    + " for writing (will try read-only):", e);
        }
        SQLiteDatabase db=null;
        try{
            isInitializing=true;
            String path=getDatabasePath(name).getPath();
            db=SQLiteDatabase.openDatabase(path,factory,SQLiteDatabase.OPEN_READWRITE);
            if(db.getVersion()!=newVersion){
                throw new SQLiteException(
                        "Can't upgrade read-only database from version "
                        +db.getVersion()+"to"  +newVersion + ":"+path);
            }
            onOpen(db);
            Log.w(TAG, "Opened " + name + " in read-only mode");
            database=db;
            return database;
        }finally {
             isInitializing=false;
            if(db!=null && db !=database)
                db.close();
        }
    }

    /**
     * 根据文件名字创建文件路径
     * @param name
     * @return
     */
    public File getDatabasePath(String name){
        String EXTERN_PATH=null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)==true){
            String dbPath=context.getString(R.string.dir)
                    +context.getString(R.string.db_dir)+ "/";
            EXTERN_PATH=Environment.getExternalStorageDirectory().getAbsolutePath()+dbPath;
            File file=new File(EXTERN_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
        }
        return new File(EXTERN_PATH+name);
    }
    public synchronized void close(){
        if(isInitializing){
            //关闭数据库期间不能进行初始化
            throw new IllegalStateException("Closed during initialization");
        }
        if(database!=null && database.isOpen()){
            database.close();
            database=null;
        }
    }
    /**
     * 创建数据库
     * @param db
     */
    public abstract void onCreate(SQLiteDatabase db);

    /**
     * 更新数据库
     * @param db
     * @param olderVersion
     * @param newVersion
     */
    public abstract void onUpgrade(SQLiteDatabase db,int olderVersion,int newVersion);

    /**
     * 打开数据库
     * @param db
     */
    public void onOpen(SQLiteDatabase db){

    }
}
