package com.example.administrator.util;

import android.widget.TextView;

/**
 * Created by Administrator on 2015/9/13
 * 验证工具.
 */
public class ValidateUtil {
    /**
     * 判断输入是否为空
     */
    public static boolean isEmpty(TextView textView,String displayStr){
        if(StringUtil.empty(textView.getText().toString().trim())){
            textView.setError(displayStr +"不能为空!" );
            textView.setFocusable(true);
            textView.requestFocus();
            return true;
        }
        return false;
    }
}
