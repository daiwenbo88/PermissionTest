package com.example.permissionutils;

/**
 * author : daiwenbo
 * e-mail : daiwwenb@163.com
 * date   : 17/4/19
 * description   : 用做于监听权限是否授权成功
 */

public interface PermissionListener {
    /**
     * 授权成功
     */
    void permissionGranted();

    /**
     * 授权失败
     */
    void permissionDenied(String[] permission);
}
