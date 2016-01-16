package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.ChartHisBean;
import com.example.administrator.model.User;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class RecentChartAdapter extends BaseAdapter {

    private LayoutInflater mInflater=null;
    private List<ChartHisBean>inviteUsers;
    private Context context;
    private View.OnClickListener contacterOnClick;

    public RecentChartAdapter(Context context,List<ChartHisBean> inviteUsers){
        this.context=context;
        mInflater=LayoutInflater.from(context);
        this.inviteUsers=inviteUsers;
    }

    public void getNoticeList(List<ChartHisBean> inviteUsers){
        this.inviteUsers=inviteUsers;
    }
    @Override
    public int getCount() {
        return inviteUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ChartHisBean notice=inviteUsers.get(position);
        Integer ppCount=notice.getNoticeSum();
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.recent_chart_item,null);
            viewHolder=new ViewHolder();
            viewHolder.itemIcon=(ImageView)convertView.findViewById(R.id.new_icon);
            viewHolder.newContent=(TextView)convertView.findViewById(R.id.new_content);
            viewHolder.newDate=(TextView)convertView.findViewById(R.id.new_date);
            viewHolder.newTitle=(TextView)convertView.findViewById(R.id.new_title);
            viewHolder.paopao=(TextView)convertView.findViewById(R.id.paopao);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        String jid=notice.getFrom();
        User user= ContacterManager.getNickName(jid, XmppConnectionManager.getInstance().getConnection());
        if(user==null){
            user=new User();
            user.setJID(jid);
            user.setName(jid);
        }
        viewHolder.newTitle.setText(user.getName());
        viewHolder.itemIcon.setBackgroundResource(R.drawable.h001);
        viewHolder.newDate.setText(notice.getNoticeTime().substring(5,16));
        viewHolder.newContent.setText(notice.getContent());
        viewHolder.newContent.setTag(user);
        if(ppCount!=null&& ppCount>0){
            viewHolder.paopao.setText(ppCount+"");
            viewHolder.paopao.setVisibility(View.VISIBLE);
        }else {
            viewHolder.paopao.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(contacterOnClick);
        return convertView;
    }

    public class ViewHolder{
        public ImageView itemIcon;
        public TextView newTitle;
        public TextView newContent;
        public TextView newDate;
        public TextView paopao;
    }

    public void setOnClickListener(View.OnClickListener contacterOnClick){
        this.contacterOnClick=contacterOnClick;
    }

}
