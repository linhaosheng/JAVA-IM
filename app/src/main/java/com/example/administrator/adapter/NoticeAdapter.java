package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.model.Notice;

import java.util.List;

/**
 * Created by Administrator on 2015/9/28.
 */
public class NoticeAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Notice>inviteNotices;
    private Context context;

    public NoticeAdapter(Context context,List<Notice>inviteNotices){
        this.context=context;
        this.inviteNotices=inviteNotices;
        this.mInflater=LayoutInflater.from(context);
    }

    public void setNoticeList(List<Notice> inviteUsers) {
        this.inviteNotices = inviteUsers;
    }
    @Override
    public int getCount() {
        return inviteNotices==null ? 0:inviteNotices.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteNotices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notice notice=inviteNotices.get(position);
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.my_notice_item,null);
            viewHolder=new ViewHolder();
            viewHolder.newTitle=(TextView)convertView.findViewById(R.id.new_title);
            viewHolder.itemIcon=(ImageView)convertView.findViewById(R.id.new_icon);
            viewHolder.newContent=(TextView)convertView.findViewById(R.id.new_content);
            viewHolder.paopao=(TextView)convertView.findViewById(R.id.paopao);
            viewHolder.newDate=(TextView)convertView.findViewById(R.id.new_date);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
          if(notice.getNoticeType()==Notice.ADD_FRIEND){    //添加好友请求  // 加气泡，处理的就消失了整体
              viewHolder.itemIcon.setBackgroundResource(R.drawable.h001);
              viewHolder.newContent.setText(notice.getContent());
          }else if (Notice.SYS_MSG==notice.getNoticeType()){    // 如果系统消息未读，加气泡
                 viewHolder.itemIcon.setBackgroundResource(R.drawable.icon_recent_sysmsg);
                 viewHolder.newContent.setText(notice.getContent());
          }
         viewHolder.newTitle.setText(notice.getTitle());
        viewHolder.newDate.setText(notice.getNoticTime().substring(5,19));
        viewHolder.newContent.setTag(notice);
        if(Notice.UNREAD==notice.getStatus()){
            viewHolder.paopao.setText("");
            viewHolder.paopao.setVisibility(View.VISIBLE);
        }else {
            viewHolder.paopao.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder{
        public ImageView itemIcon;
        public TextView newTitle;
        public TextView newContent;
        public TextView newDate;
        public TextView paopao;
    }
}
