package com.example.administrator.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2015/10/3.
 */
public class CommonUtils {

    public static final String PATH= "/sdcard/MyVoiceForder/Record/";
    public static final String PATHMG= "/sdcard/MyImageForder/Record/";
    public static final String PIC_SIGN = "<picture_YY>";
    public static final String VOICE_SIGN = "<voice_YY>";
    public static final String DIR = "dir";
    public static final String IMGNAME = "yyimg";
    public static final String VOCNAME = "yyvoc";
    public static MediaPlayer mMediaPlayer=new MediaPlayer();

    /**
     * 得到图片的base64字节码，用于发送字节流
     * @param path
     * @param name
     * @return
     */
    public static String getImageBase64(String path,String name){
      Bitmap bitmap=CommonUtils.getImage(path);
        bitmap=CommonUtils.compressImage(bitmap);

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);
        byte [] bytes=stream.toByteArray();
        BASE64Encoder encoder=new BASE64Encoder();
        String code=encoder.encode(bytes);
        String imageMsg=CommonUtils.PIC_SIGN +code+name+CommonUtils.PIC_SIGN;
        return imageMsg;
    }

    /**
     * 图片大小比例压缩方法(根据图片路径获得)
     * @param srcPath
     * @return
     */
    public static Bitmap getImage(String srcPath){
        BitmapFactory.Options newOpt=new BitmapFactory.Options();
        //开始读入图片，这时把option.injustDecodeBounds设为true
        newOpt.inJustDecodeBounds=true;
        Bitmap bitmap=BitmapFactory.decodeFile(srcPath,newOpt);

        newOpt.inJustDecodeBounds=false;
        int width=newOpt.outWidth;
        int heigth=newOpt.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为

        float hh=800f; // 这里设置高度为800f
        float ww=480f; // 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if(width>heigth && width>ww){
            be=(int)(newOpt.outWidth/ww);
        }else if (width<heigth &&heigth>hh){
            be=(int)(newOpt.outWidth/hh);
        }
        if(be<=0){
            be=1;
        }
        newOpt.inSampleSize=be;  // 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap=BitmapFactory.decodeFile(srcPath,newOpt);
        return compressImage(bitmap);
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options=90;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length/1024>100){
            baos.reset();
            options-=10; // 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG,options,baos);
        }
        ByteArrayInputStream bais=new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap=BitmapFactory.decodeStream(bais,null,null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
