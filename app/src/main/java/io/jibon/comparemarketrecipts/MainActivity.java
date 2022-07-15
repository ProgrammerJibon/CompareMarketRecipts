package io.jibon.comparemarketrecipts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    int rand = (int) Math.ceil((Math.random() * (999999 - 100000)) + 100000);
    TextView splash_text;
    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find blocks

        splash_text = findViewById(R.id.splash_text);
        //start working
        MainActivity.this.run();

    }

    public void run() {
        Intent intent = new Intent(activity, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (hasPermission(this, permissions)) {
//            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//
//            if (location != null){
//                double longitude = location.getLongitude();
//                double latitude = location.getLatitude();
//
//                try {
//                    Address geocoder = new Geocoder(activity).getFromLocation(latitude, longitude, 1).get(0);
//                    Log.e("errnos", String.valueOf(geocoder));
//                } catch (Exception e) {
//                    Log.e("errnos", e.toString());
//                }
//            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    activity.startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    activity.finish();
                }
            }, 1000);
        }else{
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    public boolean hasPermission(Context context, String[] permissionx) {
        if (context != null && permissionx != null) {
            for (String permission : permissionx) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean results = true, need_settings = false;
        int i = 0;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])){
                    need_settings = true;
                }
                results = false;
                break;
            }
            i++;
        }
        if (need_settings){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Permission needed!");
            builder.setMessage("App need permission to read contacts");
            builder.setPositiveButton("Settings", (dialogInterface, i1) -> {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                finish();
            });
            builder.setNegativeButton("Exit", (dialogInterface, i1) -> {
                activity.finish();
            });
            builder.show();
        }else if (results){
            run();
        }else{
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        //back to android home without closing app
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }
}