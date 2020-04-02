package com.pomelo.schoolar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.pomelo.schoolar.scan.ScanARActivity;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

public class MainActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanARActivity.class);
                startActivity(intent);
            }
        });
        Permissions permissions = Permissions.build(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
        requestPermissons(permissions);
    }

    private void requestPermissons(Permissions permissions) {
        //请求读取电话权限
        SoulPermission.getInstance().checkAndRequestPermissions(permissions, new CheckRequestPermissionsListener() {
            @Override
            public void onAllPermissionOk(Permission[] allPermissions) {

            }

            @Override
            public void onPermissionDenied(Permission[] refusedPermissions) {
                for (int i = 0; i < refusedPermissions.length; i++) {
                    requestPermisson(refusedPermissions[i].permissionName);
                }
            }
        });
    }

    private void requestPermisson(String permission) {
        //请求读取电话权限
        SoulPermission.getInstance().checkAndRequestPermission(permission, new CheckRequestPermissionListener() {
            @Override
            public void onPermissionOk(Permission permission) {
            }

            @Override
            public void onPermissionDenied(final Permission permission) {
                if (permission.shouldRationale) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("如果你拒绝了权限，你将无法使用该软件，请点击授予权限")
                            .setPositiveButton("授予", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermisson(permission.permissionName);
                                }
                            }).create().show();
                } else {
                    String permissionDesc = permission.getPermissionNameDesc();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage(permissionDesc + "异常，请前往设置－>权限管理，打开" + permissionDesc + "。")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //去设置页
                                    SoulPermission.getInstance().goPermissionSettings();
                                }
                            }).create().show();
                }
            }
        });
    }
}
