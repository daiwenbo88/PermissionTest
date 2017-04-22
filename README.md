# PermissionTest
权限动态申请
![](http://on96fbw9r.bkt.clouddn.com/permission.gif)

 - 检查是否已经授权
 
 ```
 /*
 *this 申请授权的Activtiy
 *permiss 申请授权权限 可以是多个权限
 */
 PermissionsUtil.hasPermission(this, permiss)
 ```
 - 申请授权操作
 ```
 /**
 *this 申请授权的Activtiy
 *PermissionListener 授权回调接口
 *permiss 申请授权权限 可以是多个权限
 *true 授权失败是否显示Dialog
 *info 显示授权失败Dialog弹窗内容
 /
PermissionsUtil.requestPermission(this, new PermissionListener(), permiss, true, info);
 ```
 - 监听授权接口
 ```
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
 ```
