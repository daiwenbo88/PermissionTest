package com.example.permissionutils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * author : daiwenbo
 * e-mail : daiwwenb@163.com
 * date   : 17/4/19
 * description   : 权限检查相关工具类
 */

public class PermissionsUtil {
    private static Map<String[],PermissionListener> listenerMap= new HashMap<>();
    /**
     * 申请权限 系统默认弹出一个dialog提示
     * @param activity 申请权限的Activtiy
     * @param listener  权限申请成功与否监听回调
     * @param permission 要申请的权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void  requestPermission(@NonNull Activity activity, @NonNull PermissionListener listener, @NonNull String...permission){
        requestPermission(activity,listener,permission,true,null);
    }

    /**
     *
     * @param activity 申请权限的Activtiy
     * @param listener 权限申请成功与否监听回调
     * @param permission 要申请的权限
     * @param showDialog 申请权限失败是否显示自定义Dialog 提示用户
     * @param info 自定义diaolog 显示内容
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermission(@NonNull Activity activity, @NonNull PermissionListener listener, @NonNull String[] permission, @NonNull boolean showDialog, DialogInfo info) {
        //保存监听回调到Map中
        listenerMap.put(permission,listener);
        //启动一个Activity来处理所有的权限申请
        PermissionActivtiy.startActivtiy( activity, listener, permission, showDialog,info);
    }

    /**
     * 判断该权限组是否授权
     * @param activtiy 申请权限的Activtiy
     * @param permission 要申请的权限
     * @return 返回true代表已经授权
     */
    public static boolean hasPermission(@NonNull Activity activtiy, @NonNull String... permission) {
        for(String per:permission){
            int result= PermissionChecker.checkSelfPermission(activtiy,per);
            if(PermissionChecker.PERMISSION_GRANTED!=result){
                return false;
            }
        }
        return true;
    }
    /**
     * 根据返回码来判断是否为授权成功码
     * @param grantResults
     * @return true授权成功
     */
    public static boolean isGranted(int[] grantResults) {
        for(int result:grantResults){
            if(PermissionChecker.PERMISSION_GRANTED!=result){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param permission 要申请的权限
     * @return 获取监听回调
     */
    static PermissionListener removeListener(String[] permission) {
        return listenerMap.remove(permission);
    }

    public static void startAppSetting(@NonNull Activity activtiy) {
        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:"+activtiy.getPackageName()));
        activtiy.startActivity(intent);
    }


    public static class DialogInfo implements Serializable{
        private static final long serialVersionUID = 1L;
        public String title;
        public String Content;
        public String cancel;
        public String ensure;
    }
}
