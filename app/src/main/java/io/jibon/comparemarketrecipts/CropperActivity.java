package io.jibon.comparemarketrecipts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {
    Activity activity;
    String result;
    Uri fileUril;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        activity = this;
        readIntent();

        String dest_uri = UUID.randomUUID().toString() + ".jpg";
        UCrop.Options options = new UCrop.Options();
        options.setBrightnessEnabled(true);
        options.setContrastEnabled(true);
        UCrop.of(fileUril, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withMaxResultSize(3840, 3840)
                .start(CropperActivity.this);
        
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            result = intent.getStringExtra("DATA");
            fileUril = Uri.parse(result);

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