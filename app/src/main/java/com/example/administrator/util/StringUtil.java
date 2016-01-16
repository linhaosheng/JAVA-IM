package com.example.administrator.util;

/**
 * Created by Administrator on 2015/9/13.
 */
public class StringUtil {
    /**
     * 处理空字符串
     */
    public static String doEmpty(String str){
            return doEmpty(str,"");
    }

    /**
     * 处理空字符串
     * @param str
     * @param defaultValue
     * @return
     */
    public static String doEmpty(String str,String defaultValue){
        if(str==null ||str.equalsIgnoreCase("null") ||str.trim().equals("") || str.trim().equals("请选择")){
            str=defaultValue;
        }else if(str.startsWith("null")){
            str=str.substring(4,str.length());
        }
        return str.trim();
    }
    public static boolean empty(Object o){
        if(o==null || "".equals(o.toString().trim()) || "null".equalsIgnoreCase(o.toString().trim()) || "undefine".equalsIgnoreCase(o.toString().trim())){
          return  true;
        }else{
            return false;
        }
    }
    final static String PLEASE_SELECT = "请选择...";
    public static boolean notEmpty(Object o){
        return o!=null && !"".equals(o.toString().trim())
                && !"null".equalsIgnoreCase(o.toString().trim())
        && !"undefined".equalsIgnoreCase(o.toString().trim())
        && !PLEASE_SELECT.equals(o.toString().trim());
    }

    /***
     * 返回用户名JID
     * @param userName
     * @param jidFor 域名//如183.58.237.131
     * @return
     */
    public static String getJidByName(String userName,String jidFor){
           if(empty(userName) || empty(jidFor)){
               return  null;
           }
        return userName+"@"+jidFor;

    }
    /**
     * 给用户名返回JID,使用默认域名183.58.237.131
     *
     * @param userName
     * @return
     */
    public static String getJidByName(String userName) {
        String jidFor = "183.58.237.131";
        return getJidByName(userName, jidFor);
    }
    /**
     * 给JID返回用户名
     */
    public static String getUserNameByJid(String jid){
        if(empty(jid)){
            return null;
        }
        if(!jid.contains("@")){
            return jid;
        }
        return jid.split("@")[0];
    }
}
