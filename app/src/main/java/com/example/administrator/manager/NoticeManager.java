package com.example.administrator.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.example.administrator.common.Constant;
import com.example.administrator.dao.DBManager;
import com.example.administrator.dao.SQLiteTemplate;
import com.example.administrator.model.Notice;
import com.example.administrator.util.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/9/15.
 */
public class NoticeManager {
    private static NoticeManager noticeManager;
    private static DBManager manager=null;

    private NoticeManager(Context context){
        SharedPreferences sharedPre=context.getSharedPreferences(Constant.LOGIN_SET,Context.MODE_PRIVATE);
        String databaseName=sharedPre.getString(Constant.USERNAME,null);
        manager=DBManager.getInstance(context, databaseName);
    }
    public static NoticeManager getInstance(Context context){
        if(noticeManager==null){
            noticeManager=new NoticeManager(context);
        }
        return noticeManager;
    }

    /**
     * 保存消息
     * @param notice
     * @return
     */
    public long saveNotice(Notice notice){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        ContentValues contentValues=new ContentValues();
        if(StringUtil.notEmpty(notice.getTitle())){
            contentValues.put("title",StringUtil.doEmpty(notice.getTitle()));
        }
        if(StringUtil.notEmpty(notice.getContent())){
            contentValues.put("content",StringUtil.doEmpty(notice.getContent()));
        }
        if(StringUtil.notEmpty(notice.getTo())){
            contentValues.put("notice_to",StringUtil.doEmpty(notice.getTo()));
        }
        if(StringUtil.notEmpty(notice.getFrom())){
            contentValues.put("notice_from",StringUtil.doEmpty(notice.getFrom()));
        }
        contentValues.put("type",notice.getNoticeType());
        contentValues.put("status",notice.getStatus());
        contentValues.put("notice_time",notice.getNoticTime());
        return st.insert("im_notice",contentValues);
    }

    /**
     * 获得未读的集合
     * @return
     */
    public List<Notice>getUnReadNoticeList(){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        List<Notice>list=st.queryForList(new SQLiteTemplate.RowMapper<Notice>() {
            @Override
            public Notice mapRow(Cursor cursor, int index) {
               Notice notice=new Notice();
                notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
                notice.setContent(cursor.getString(cursor.getColumnIndex("content")));
                notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
                notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
                notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                return notice;
            }
        },"select * from im_notice where status=" + Notice.UNREAD + "", null);
        return list;
    }

    /**
     * 更新状态
     * @param id
     * @param status
     */
    public void updateStatus(String id,Integer status){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",status);
        st.updateById("im_notice",id,contentValues);
    }

    /**
     * 更新某人所有通知状态.
     * @param xfrom
     * @param status
     */
    public void updateStatusByFrom(String xfrom,Integer status){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",status);
        st.update("im_notice",contentValues,"notice_from=?",new String[]{""+ xfrom });
    }

    /**
     * 更新添加好友状态
     * @param id
     * @param status
     * @param content
     */
    public void updateAddFriendStatus(String id,Integer status,String content){
       SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",status);
        contentValues.put("content",content);
        st.updateById("im_notice",id,contentValues);
    }

    /**
     * 查询未读的信息
     * @param type
     * @return
     */
    public Integer getUnReadNoticeCountByType(int type){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        return st.getCount("select _id from im_notice where status=? and type=?",new String[]{"" + Notice.UNREAD, "" +type});
    }
    /**
     * 删除与某人的通知
     */
    public int delNoticeHisWithSb(String fromUser){
        if(StringUtil.empty(fromUser)){
            return 0;
        }
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        return st.deleteByCondition("im_notice","notice_from=?",new String[]{""+fromUser});
    }

    /**
     * 分页获取所有聊消息.(分类)1 好友添加 2系统 消息 3 聊天 降序排列
     * @param isRead  0 已读 1未读  2 全部
     * @return
     */
    public List<Notice>getNoticeListByTypeAndPage(int isRead){

        StringBuilder sb=new StringBuilder();
        String []str=null;
        sb.append("select *from im_notice where type in(1,2)");
        if(Notice.UNREAD==isRead ||Notice.READ==isRead){
            str=new String[]{""+isRead};
            sb.append(" and status=? ");
        }
        sb.append(" order by notice_time desc");
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        List<Notice>list=st.queryForList(new SQLiteTemplate.RowMapper<Notice>() {
            @Override
            public Notice mapRow(Cursor cursor, int index) {
                Notice notice=new Notice();
                notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
                notice.setContent(cursor.getString(cursor.getColumnIndex("content")));
                notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
                notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("type")));
                notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
                notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                notice.setNoticTime(cursor.getString(cursor.getColumnIndex("notice_time")));
                return notice;
            }
        }, sb.toString(), str);
        return list;
    }
    /**
     * 删除全部记录
     */
    public void delALL(){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        st.execSQL("delete from im_notice");
    }
    /**
     * 根据主键获取消息
     */
    public Notice getNoticeById(String id){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        return st.queryForObject(new SQLiteTemplate.RowMapper<Notice>() {
            @Override
            public Notice mapRow(Cursor cursor, int index) {
                Notice notice=new Notice();
                notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
                notice.setContent(cursor.getString(cursor.getColumnIndex("content")));
                notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
                notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
                notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("type")));
                notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                return notice;
            }
        },"select * from im_notice where _id=?",new String[]{ id });
    }
    /**
     * 根据主键删除信息
     */
    public void delById(String noticeId){
        SQLiteTemplate st=SQLiteTemplate.getInstance(manager,false);
        st.deleteById("im_notice",noticeId);
    }
}
