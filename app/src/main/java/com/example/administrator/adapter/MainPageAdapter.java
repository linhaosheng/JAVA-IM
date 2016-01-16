package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.model.MainPageItem;
import com.example.administrator.model.Notice;

import java.util.List;

/**
 * Created by Administrator on 2015/9/15.
 */
public class MainPageAdapter extends BaseAdapter {
    private Context context;
    private List<MainPageItem>list;
    private LayoutInflater inflater;

    public MainPageAdapter(Context context){
        this.context=context;
    }
    public void setList(List<MainPageItem>list){
         this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.main_page_item,null);
            viewHolder=new ViewHolder();
            viewHolder.appImage=(ImageView)convertView.findViewById(R.id.itemImage);
            viewHolder.appName=(TextView)convertView.findViewById(R.id.itemText);
            viewHolder.paopao=(TextView)convertView.findViewById(R.id.paopao);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if(position<=1){
            NoticeManager noticeManager=NoticeManager.getInstance(context);
            Integer unreadCount=0;
            if(position==0){
                unreadCount=noticeManager.getUnReadNoticeCountByType(Notice.CHAT_MSG);
            }else if(position==1){
                Integer countAdd=noticeManager.getUnReadNoticeCountByType(Notice.ADD_FRIEND);
                Integer countSys=noticeManager.getUnReadNoticeCountByType(Notice.SYS_MSG);
                countAdd=(countAdd==null ? 0 :countAdd);
                countSys=(countSys== null ? 0:countSys);
                unreadCount=countAdd+countSys;
            }
            if(unreadCount>0){
                viewHolder.paopao.setText(""+unreadCount);
                viewHolder.paopao.setVisibility(View.VISIBLE);
            }else{
                viewHolder.paopao.setVisibility(View.GONE);
            }
        }
        MainPageItem info=list.get(position);
        if(info!=null){
            viewHolder.appName.setText(info.getName());
            viewHolder.appImage.setImageResource(info.getImage());
        }
        return convertView;
    }
    public class ViewHolder{
        public ImageView appImage;
        public TextView appName;
        public TextView paopao;
    }
}
