package com.example.administrator.model;

/**
 * Created by Administrator on 2015/9/15.
 */
public class MainPageItem {
    private String name;
    private Integer image;

    public MainPageItem(String name, Integer image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
