package com.example.administrator.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/10/2.
 * 该类为线程池，把各种耗时操作都加到线程池里，充分的利用系统资源
 */
public class ThreadPoolManager {

    private ExecutorService service;
     private static ThreadPoolManager poolManager;

    public static ThreadPoolManager getInstance(){
        if(poolManager==null){
            poolManager=new ThreadPoolManager();
        }
        return poolManager;
    }
    private ThreadPoolManager(){
        //获取系统分配的最大资源数
        int num=Runtime.getRuntime().availableProcessors();
        service= Executors.newFixedThreadPool(num*4);
    }
    public void addTask(Runnable runnable){

       service.execute(runnable);
    }
}
