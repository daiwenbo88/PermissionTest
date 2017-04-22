package com.example.permissionutils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class PermissionActivtiy extends AppCompatActivity {
    private static final String PERMSSION = "permssion";
    private static final String SHOW_DIALOG = "showdialog";
    private static final String INFO = "info";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private final String defaultTitle = "友情提示";
    private final String defaultContent = "为了更好的为您提供服务,我们需要您的授权。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
    private final String defaultCancel = "取消";
    private final String defaultEnsure = "设置";

    private boolean isRequireCheck;//是否已经请求过权限
    private boolean isShowDialog;//是否显示自定义dialog
    private String[] permission;//申请的权限组
    private PermissionsUtil.DialogInfo info;//自定义dialog显示内容

    public static void startActivtiy(@NonNull Activity activity,
                                     @NonNull PermissionListener listener,
                                     @NonNull String[] permission,
                                     @NonNull boolean showDialog,
                                     PermissionsUtil.DialogInfo info) {

        Intent intent = new Intent(activity, PermissionActivtiy.class);
        intent.putExtra(PERMSSION, permission);
        intent.putExtra(SHOW_DIALOG, showDialog);
        intent.putExtra(INFO, info);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null == intent || !intent.hasExtra("permssion")) {
            finish();
            return;
        }
        isRequireCheck = true;
        permission = intent.getStringArrayExtra(PERMSSION);
        isShowDialog = intent.getBooleanExtra(SHOW_DIALOG, true);
        info = (PermissionsUtil.DialogInfo) getIntent().getSerializableExtra(INFO);

        if (null == info) {
            info = new PermissionsUtil.DialogInfo();
            info.title = defaultTitle;
            info.Content = defaultContent;
            info.cancel = defaultCancel;
            info.ensure = defaultEnsure;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            //检查是否授权
            if (PermissionsUtil.hasPermission(this, permission)) {
                permissionsGranted();
            } else {
                //申请权限
                requestPermissions();
                isRequireCheck = false;
            }

        } else {
            isRequireCheck = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //要用PermissionChecker二次判断
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            permissionsGranted();
        } else if (isShowDialog) {
            showMissingPermissionDialog();
        } else {
            permissionsDenied();
        }
    }


    /**
     * dialog弹窗提示 该权限的重要性
     */
    private void showMissingPermissionDialog() {
        new AlertDialog.Builder(this,R.style.AlertDialog)
                .setTitle(info.title)
                .setMessage(info.Content)
                .setNegativeButton(info.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //取消
                        permissionsDenied();
                    }
                })
                .setPositiveButton(info.ensure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //打开应用权限设置
                        PermissionsUtil.startAppSetting(PermissionActivtiy.this);

                    }
                })
                .show();
    }

    // 请求权限兼容低版本
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
    }

    /**
     * 权限均已授权
     */
    private void permissionsGranted() {
        PermissionListener listener = PermissionsUtil.removeListener(permission);
        if (null != listener) {
            //权限授权成功
            listener.permissionGranted();
        }
        finish();
    }

    /**
     * 授权失败
     */
    private void permissionsDenied() {
        PermissionListener listener = PermissionsUtil.removeListener(permission);
        if (null != listener) {
            //权限授权失败
            listener.permissionDenied(permission);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        PermissionsUtil.removeListener(permission);
        super.onDestroy();

    }
}
