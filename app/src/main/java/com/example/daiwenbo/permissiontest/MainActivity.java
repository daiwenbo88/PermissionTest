package com.example.daiwenbo.permissiontest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.Toast;

import com.example.permissionutils.PermissionListener;
import com.example.permissionutils.PermissionsUtil;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] CALLLOG_PROJECTION = new String[]{CallLog.Calls._ID, CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE};
    private String[] permiss=new String[]{Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE};
    private ListView listview;
    private MyCursorAdapter mAdapter;
    private boolean isRequest=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        mAdapter = new MyCursorAdapter(MainActivity.this, null);
        listview.setAdapter(mAdapter);


    }
    private void getData() {
        final MyLoaderCallback callback = new MyLoaderCallback();
        if (PermissionsUtil.hasPermission(this, permiss)) {
            getLoaderManager().initLoader(0, null, callback);
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            Log.e(TAG,"device_id:"+DEVICE_ID);
        } else {
            PermissionsUtil.DialogInfo info = new PermissionsUtil.DialogInfo();
            info.title = "友情提示";
            info.Content = "为了更好的为您提供服务,我们需要您对查看通讯录进行授权。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
            info.cancel = "取消";
            info.ensure = "设置";
            if (isRequest) {
                isRequest=false;
                PermissionsUtil.requestPermission(this, new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_LONG).show();
                        getLoaderManager().initLoader(0, null, callback);
                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String DEVICE_ID = tm.getDeviceId();
                        Log.e(TAG, "device_id:" + DEVICE_ID);
                    }

                    @Override
                    public void permissionDenied(String[] permission) {
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_LONG).show();
                        permiss = null;
                    }
                }, permiss, true, info);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

            getData();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private class MyLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader loader = new CursorLoader(MainActivity.this, CallLog.Calls.CONTENT_URI, CALLLOG_PROJECTION,
                    null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (null == data) {
                return;
            }
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    }

}
