package com.example.administrator.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.administrator.adapter.GuidePageAdapter;
import com.example.administrator.task.ThreadPoolManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/13.
 * Android 实现左右滑动指引效果
 */
public class GuideViewActivity extends ActivitySupport{
    private ViewPager viewPager;
    private ArrayList<View>pageViews;
    private ImageView imageView;
    private ImageView[]imageViews;
    //包裹滑动的图片LinearLayout
    private ViewGroup main;
    //包裹小圆点的LinearLayout
    private ViewGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getIMApplication().addActivity(this);
        LayoutInflater inflater=getLayoutInflater();
        pageViews=new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.item01,null));
        pageViews.add(inflater.inflate(R.layout.item02,null));
        pageViews.add(inflater.inflate(R.layout.item03,null));
        pageViews.add(inflater.inflate(R.layout.item04,null));
        pageViews.add(inflater.inflate(R.layout.item05, null));
        pageViews.add(inflater.inflate(R.layout.item06, null));
        View view=new View(this);
        view.setBackgroundResource(R.color.white);
        pageViews.add(view);

        imageViews=new ImageView[pageViews.size()];
        main=(ViewGroup)inflater.inflate(R.layout.guide_view,null);

        group=(ViewGroup)main.findViewById(R.id.viewGroup);
        viewPager=(ViewPager)main.findViewById(R.id.guidePages);

        for(int i=0;i<pageViews.size()-1;i++){
            imageView=new ImageView(GuideViewActivity.this);
            imageView.setLayoutParams(new ActionBar.LayoutParams(20,20));
            imageView.setPadding(20,0,20,0);
            imageViews[i]=imageView;
            if(i==0){
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else {
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);
            }
            group.addView(imageViews[i]);
        }
        setContentView(main);
        GuidePageAdapter pageAdapter=new GuidePageAdapter(pageViews);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }
    // 指引页面数据适配器



    //指引页面更改事件监听器
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int arg0) {
         if(arg0==imageViews.length-1){

             Runnable runnable=new Runnable() {
                 @Override
                 public void run() {
                     GuideViewActivity.this.startActivity(new Intent(GuideViewActivity.this, MainActivity.class));
                 }
             };
             ThreadPoolManager.getInstance().addTask(runnable);
         }else{
             for(int i=0;i<imageViews.length-1;i++){
                 imageViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
                 if(arg0 != i){
                     imageViews[i].setBackgroundResource(R.drawable.page_indicator);
             }
             }
         }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
