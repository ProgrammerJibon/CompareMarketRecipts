package io.jibon.comparemarketrecipts;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.CropImageView;

public class Home extends AppCompatActivity {
    Button button_capture, button_copy;
    TextView text_data;
    Activity activity;
    ImageView image_crop_view;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;

        button_capture = activity.findViewById(R.id.button_capture);
        button_copy = activity.findViewById(R.id.button_copy);
        text_data = activity.findViewById(R.id.text_data);
        image_crop_view = activity.findViewById(R.id.image_view_crop);


        button_capture.setOnClickListener(view -> {
            mGetContent.launch("image/*");
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(activity, CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 10118);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 10118){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if (result != null){
                resultUri = Uri.parse(result);
            }
            image_crop_view.setImageURI(resultUri);
        }
    }
}