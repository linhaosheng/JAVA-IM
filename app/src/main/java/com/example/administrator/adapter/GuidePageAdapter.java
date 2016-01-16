package com.example.administrator.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/14.
 */
public class GuidePageAdapter extends PagerAdapter {
    private ArrayList<View> pageViews;
    public GuidePageAdapter(ArrayList<View> pageViews) {
        this.pageViews=pageViews;
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==o;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager)container).removeView(pageViews.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager)container).addView(pageViews.get(position));
        return pageViews.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        //super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState(){
        return null;
    }

    @Override
    public void startUpdate(View container) {
        //super.startUpdate(container);
    }

    @Override
    public void finishUpdate(View container) {
        //super.finishUpdate(container);
    }

}

