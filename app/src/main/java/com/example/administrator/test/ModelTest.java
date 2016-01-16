package com.example.administrator.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.example.administrator.dao.DBManager;
import com.example.administrator.dao.SQLiteTemplate;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.model.Notice;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.packet.Presence;

import java.util.List;

/**
 * Created by Administrator on 2015/10/1.
 */
public class ModelTest extends AndroidTestCase {

    public void test() throws Exception {

        DBManager manager = DBManager.getInstance(getContext(), "im_notice1");
        SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
        ContentValues contentValues=new ContentValues();

   /*     contentValues.put("title","title1");
        contentValues.put("content","content1");
        contentValues.put("notice_to","notice_to1");
        contentValues.put("notice_from","notice_from1");
        contentValues.put("type",2);
        contentValues.put("status",2);
        contentValues.put("notice_time","notice_time1");
        long a= st.insert("im_notice",contentValues);
      */


        contentValues.put("msg_from","test5");
        contentValues.put("msg_type",1);
        contentValues.put("msg_time","123");
        long a= st.insert("im_msg_his",contentValues);
        System.out.print(a);

    }



    class Persion{
        private String id;
        private String name;
        private int age;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
