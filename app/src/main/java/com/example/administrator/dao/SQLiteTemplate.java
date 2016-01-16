package com.example.administrator.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/15.
 * SQLite 数据库模板类
 * 该类提供了数据库操作常用的增删改查,以及各种复杂的条件配置，分页，排序等等
 */
public class SQLiteTemplate {
    /**
     * default primary key
     */
    protected String mPrimaryKey="_id";
    /**
     * DBManager
     */
    private DBManager dbManager;
    /**
     * 是否为一个事务
     */
    private boolean isTransaction=false;
    /**
     * get the SQLiteTemplate
     */
    private static SQLiteTemplate sqLiteTemplate=null;
    /**
     * 数据库连接
     */
    SQLiteDatabase database;

    public SQLiteTemplate(){}

    private SQLiteTemplate(DBManager dbManager,boolean isTransaction){
        this.dbManager=dbManager;
        this.isTransaction=isTransaction;
    }
    /**
     * isTransaction 是否属于一个事务 注：一旦isTansaction设为true
     * 所有的SQLiteTemplate 方法都不会自动关闭资源，需在事务成功后，手动关闭
     */
    public static SQLiteTemplate getInstance(DBManager dbManager,boolean isTransaction){
        if(sqLiteTemplate==null){
            sqLiteTemplate=new SQLiteTemplate(dbManager,isTransaction);
        }
        return sqLiteTemplate;
    }
    /**
     * 执行一条sql语句
     */
    public void execSQL(String sql){
        try{
          database=dbManager.openDatabase();
            database.execSQL(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                 closeDatabase(null);
            }
        }
    }

    /**
     * 执行一条sql语句
     * @param sql
     * @param bindArgs
     */
    public void execSQL(String sql,Object[] bindArgs){
        try{
            database=dbManager.openDatabase();
            database.execSQL(sql, bindArgs);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction)
            closeDatabase(null);
        }
    }

    /**
     * 向数据库中插入一条语句
     * @param table
     * @param contentValues
     * @return
     */
   public long insert(String table,ContentValues contentValues){
       try{
           database=dbManager.openDatabase();
           // insert方法第一参数：数据库表名，第二个参数如果CONTENT为空时则向表中插入一个NULL,第三个参数为插入的内容
           return database.insert(table,null,contentValues);
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           if(!isTransaction){
               closeDatabase(null);
           }
       }
       return 0;
   }

    /**
     * 批量删除指定的数据
     * @param table
     * @param primaryKey
     */
    public void deleteByIds(String table,Object... primaryKey){
        try{
            if(primaryKey.length>0){
                StringBuffer sb=new StringBuffer();
                for(Object id:primaryKey){
                    sb.append("?").append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                database=dbManager.openDatabase();
                database.execSQL("delete from" + table + "where" + mPrimaryKey + "in(" + sb + ")",(Object[]) primaryKey);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
    }

    /**
     * 根据某个字段和值删除一行数据，如name="jack
     * @param table
     * @param field
     * @param value
     * @return  返回值大于0表示删除成功
     */
    public int deleteByFiled(String table,String field,String value){
        try{
            database=dbManager.openDatabase();
            return database.delete(table,field + "=?" , new String[]{value});
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return 0;
    }

    /**
     *
     * @param table  表名
     * @param whereClause  查询语句 参数采用
     * @param whereArgs    参数值
     * @return返回值大于0表示删除成功
     */
    public int deleteByCondition(String table,String whereClause,String[] whereArgs){
        try{
            database=dbManager.openDatabase();
            return database.delete(table,whereClause,whereArgs);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return 0;
    }

    /**
     * 根据主键删除一行数据
     * @param table
     * @param id
     * @return  返回值大于0表示删除成功
     */
    public int deleteById(String table,String id){
        try{
            database=dbManager.openDatabase();
            return deleteByFiled(table, mPrimaryKey, id);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return 0;
    }

    /**
     * 根据主键更新表
     * @param table
     * @param id
     * @param contentValues
     * @return 返回值大于0表示更新成功
     */
    public int updateById(String table,String id,ContentValues contentValues){
        try{
            database=dbManager.openDatabase();
            return database.update(table, contentValues, mPrimaryKey + "=?", new String[]{id});
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return 0;
    }

    /**
     * 更新数据库
     * @param table
     * @param contentValues
     * @param whereClause
     * @param whereArgs
     * @return返回值大于0表示更新成功
     */
    public int update(String table,ContentValues contentValues,String whereClause,String[] whereArgs){
        try{
            database=dbManager.openDatabase();
            database.update(table,contentValues,whereClause,whereArgs);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return 0;
    }

    /**
     * 根据主键查看某条数据是否存在
     * @param table
     * @param id
     * @return
     */
    public Boolean isExistsById(String table,String id){
        try{
            database=dbManager.openDatabase();
            return isExistsByField(table,mPrimaryKey,id);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(null);
            }
        }
        return null;
    }
      public Boolean isExistsByField(String table,String field,String value){
          StringBuilder sql=new StringBuilder();
          sql.append("SELECT COUNT(*) FROM ").append(table).append(" WHERE ").append(field).append(" =?");
          try{
              database=dbManager.openDatabase();
              return isExistsBySQL(sql.toString(),new String[]{value});
          }catch (Exception e){
              e.printStackTrace();
          }finally {
              if(!isTransaction){
                  closeDatabase(null);
              }
          }
          return null;
      }
    /**
     * 根据sql语句来查看某条数据是否存在
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Boolean isExistsBySQL(String sql,String[] selectionArgs){
        Cursor cursor=null;
        try{
            database=dbManager.openDatabase();
            cursor=database.rawQuery(sql,selectionArgs);
            if(cursor.moveToFirst()){
                return (cursor.getInt(0)>0);
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(cursor);
            }
        }
        return null;
    }

    /**
     * 查询一条数据
     * @param rowMapper
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    public <T> T queryForObject(RowMapper<T> rowMapper,String sql,String[] args){
        Cursor cursor=null;
        T object=null;
        try{
            database=dbManager.openDatabase();
            cursor=database.rawQuery(sql,args);
            if(cursor.moveToFirst()){
                object=rowMapper.mapRow(cursor,cursor.getCount());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(cursor);
            }
        }
        return object;
    }

    /**
     * 查询
     * @param rowMapper
     * @param sql
     * @param selectionArgs     开始索引 注:第一条记录索引为0
     * @param <T>
     * @return
     */
      public <T>List<T> queryForList(RowMapper<T> rowMapper,String sql,String[] selectionArgs){
             Cursor cursor=null;
          List<T>list=null;
          try{
              database=dbManager.openDatabase();
              cursor=database.rawQuery(sql,selectionArgs);
              list=new ArrayList<T>();
              while(cursor.moveToNext()){
                  list.add(rowMapper.mapRow(cursor,cursor.getPosition()));
              }
          }catch (Exception e){
              e.printStackTrace();
          } finally {
              if(!isTransaction){
                  closeDatabase(cursor);
              }
          }
          return list;
      }

    /**
     * 分页查询
     * @param rowMapper
     * @param sql
     * @param startResult  开始索引  注：第一条记录索引为0
     * @param maxResult    不长
     * @param <T>
     * @return
     */
    public <T>List<T>queryForList(RowMapper<T> rowMapper,String sql,int startResult,int maxResult){
        Cursor cursor=null;
        List<T>list=null;
        try{
            database=dbManager.openDatabase();
            cursor=database.rawQuery(sql+"limit ?,?",new String[]{String.valueOf(startResult),String.valueOf(maxResult)});
            while(cursor.moveToNext()){
                list.add(rowMapper.mapRow(cursor,cursor.getPosition()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(cursor);
            }
        }
        return list;
    }

    /**
     * 获取记录数
     * @param sql
     * @param args
     * @return   返回0表示记录数为0
     */
    public Integer getCount(String sql,String[] args){
        Cursor cursor=null;
        try{
            database=dbManager.openDatabase();
            cursor=database.rawQuery("select count(*)from (" +sql+ ")",args);
            if(cursor.moveToNext()){
                return cursor.getInt(0);
            }
        }catch (Exception e){

            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(cursor);
            }
        }
        return 0;
    }

    /**
     * 分页查询
     * @param rowMapper
     * @param table  检索的表
     * @param columns     由需要返回的列名所组成的字符串组，传人null会返回所有的列
     * @param selection    查询条件子句，相当于select语句where关键字的后面部分，在条件字句允许使用占位符“？”
     * @param selectionArgs  对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常
     * @param groupBy     对结果级进行分组
     * @param having      对结果集进行过滤，传入null则不过滤
     * @param orderBy    对查询的结果进行排序
     * @param limit      指定偏移量和获取记录数，相当于select语句limit关键字后面的部分，如果为null则返回所有行
     * @param <T>
     * @return
     */
    public <T> List<T>queryForList(RowMapper<T>rowMapper,String table,String[]columns,
                                   String selection,String[] selectionArgs,String groupBy,
                                   String having,String orderBy,String limit){
        List<T>list=null;
        Cursor cursor=null;
        try{
            database=dbManager.openDatabase();
            cursor=database.query(table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
            list=new ArrayList<T>();
            while(cursor.moveToNext()){
                list.add(rowMapper.mapRow(cursor,cursor.getPosition()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isTransaction){
                closeDatabase(cursor);
            }
        }
        return list;
    }

    /**
     * 获得   Primary Key
     * @return
     */
    public String getPrimary(){
        return mPrimaryKey;
    }
    public void setmPrimaryKey(String primaryKey){
        this.mPrimaryKey=primaryKey;
    }
    /**
     *
     * @param <T>
     */
    public interface RowMapper<T>{
        /**
         *
         * @param cursor  游标
         * @param index   下标
         * @return
         */
        public T mapRow(Cursor cursor,int index);
    }
    /**
     * 关闭数据库
     * @param cursor
     */
    public void closeDatabase(Cursor cursor){
        if(database!=null){
            database.close();
        }
        if(cursor!=null){
            cursor.close();
        }
    }
}
