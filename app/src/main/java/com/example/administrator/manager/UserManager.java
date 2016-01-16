package com.example.administrator.manager;

import android.content.Context;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/9/15.
 */
public class UserManager {
    private static UserManager userManager=null;
    public UserManager(){}

    public static UserManager getInstance(Context context){
        if(userManager==null){
            userManager=new UserManager();
        }
        return userManager;
    }

    /**
     * 获取用户的vCard信息
     * @param jid
     * @return
     */
    public VCard getUserVCard(String jid){
        XMPPConnection xmppConnection=XmppConnectionManager.getInstance().getConnection();
        VCard vCard=new VCard();
        try{
            vCard.load(xmppConnection,jid);
        }catch (XMPPException e){
            e.printStackTrace();
        }
        return vCard;
    }

    /**
     * 保存用户的vCard信息 修改VCard时，头像会丢失 此处为asmack.jar的bug，目前还无法修复
     * @param vCard
     * @return
     */
   public VCard saveUserVCard(VCard vCard){
         XMPPConnection xmppConnection=XmppConnectionManager.getInstance().getConnection();
       try{
          vCard.save(xmppConnection);
           return getUserVCard(vCard.getJabberId());
       }catch (XMPPException e){
           e.printStackTrace();
       }
       return null;
    }

    /**
     * 获取用户头像信息
     * @param jid
     * @return
     */
    public InputStream getUserImage(String jid){
        XMPPConnection xmppConnection=XmppConnectionManager.getInstance().getConnection();
        InputStream inputStream=null;
        try{
            System.out.println("获取用户头像信息: " + jid);
            VCard vCard=new VCard();
            vCard.load(xmppConnection,jid);
            if(vCard==null || vCard.getAvatar()==null){
                return null;
            }
            ByteArrayInputStream bais=new ByteArrayInputStream(vCard.getAvatar());
            return bais;
        }catch (XMPPException e){
            e.printStackTrace();;
        }
        return inputStream;
    }
}
