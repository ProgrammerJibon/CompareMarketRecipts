package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {
    Activity activity;
    Uri fileUril;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        activity = this;
        readIntent();

        String dest_uri = UUID.randomUUID().toString() + ".jpg";
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setBrightnessEnabled(false);
        options.setContrastEnabled(false);
        options.setSaturationEnabled(false);
        options.setSharpnessEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            options.setCropFrameColor(activity.getColor(R.color.primary));
            options.setCropGridColor(activity.getColor(R.color.primary));
            options.setStatusBarColor(activity.getColor(R.color.primary));
            options.setCropGridCornerColor(activity.getColor(R.color.secondary));
            options.setToolbarColor(activity.getColor(R.color.primary));
            options.setActiveControlsWidgetColor(activity.getColor(R.color.primary));
        }
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
        if (requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri + "");
            setResult(-1, returnIntent);
            finish();
        }else{
            final Throwable ucrop_error = UCrop.getError(data);
            assert ucrop_error != null;
            Toast.makeText(activity, "Something maybe not working\n"+ucrop_error.getMessage(), Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }
}