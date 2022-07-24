package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {
    Activity activity;
    Uri fileUril;
    String dest_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        activity = this;
        readIntent();

        dest_uri = UUID.randomUUID().toString() + ".jpg";
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setBrightnessEnabled(false);
        options.setContrastEnabled(false);
        options.setSaturationEnabled(false);
        options.setSharpnessEnabled(false);
        options.setToolbarTitle("Zoom and Crop only\nproducts and prices part");
        options.setToolbarWidgetColor(activity.getColor(R.color.white));
        options.setCropFrameColor(activity.getColor(R.color.primary));
        options.setCropGridColor(activity.getColor(R.color.primary));
        options.setStatusBarColor(activity.getColor(R.color.primary));
        options.setCropGridCornerColor(activity.getColor(R.color.secondary));
        options.setToolbarColor(activity.getColor(R.color.primary));
        options.setActiveControlsWidgetColor(activity.getColor(R.color.primary));

        UCrop.of(fileUril, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withMaxResultSize(3840, 3840)
                .withOptions(options)
                .start(CropperActivity.this);
        
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            fileUril = intent.getParcelableExtra("IMG_URI");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == UCrop.REQUEST_CROP && resultCode != UCrop.RESULT_ERROR) {
                final Uri resultUri = UCrop.getOutput(data);
                Intent returnIntent = new Intent();
                if (resultUri != null) {
                    returnIntent.putExtra("RESULT", resultUri + "");
                    setResult(-1, returnIntent);
                    finish();
                } else {
                    returnIntent.putExtra("RESULT", "");
                    setResult(-1, returnIntent);
                    finish();
                }

            } else {
                Toast.makeText(activity, "Something maybe not working\n", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("RESULTX", fileUril.toString());
                setResult(-2, returnIntent);
                finish();
            }
        }catch (Exception error){
            Toast.makeText(activity, "No cropping done!", Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULTX", fileUril.toString());
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            setResult(-2, returnIntent);
            finish();
        }
    }
}