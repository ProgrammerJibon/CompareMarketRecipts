package io.jibon.comparemarketrecipts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Home extends AppCompatActivity {
    Button button_next, button_recrop;
    Activity activity;
    ImageView image_crop_view;
    ActivityResultLauncher<String> mGetContent1;
    Uri selected_image_uri, dest_uri;
    Bitmap selected_image_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        activity = this;

        image_crop_view = activity.findViewById(R.id.image_view_crop);
        button_next = activity.findViewById(R.id.button_next);
        button_recrop = activity.findViewById(R.id.button_recrop);


        button_recrop.setOnClickListener(view -> {
            if (selected_image_uri != null){
                cropperActivity(selected_image_uri);
            }
        });

        button_next.setOnClickListener(view -> {
            if (selected_image_uri == null){
                Toast.makeText(activity, "Please select an image first...", Toast.LENGTH_LONG).show();
            }else{
                try {
                    selected_image_bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selected_image_uri);
                    getTextFromImage(selected_image_bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        image_crop_view.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("Choose an option");
            dialog.setPositiveButton("Gallery", (dialogInterface, i) -> {
                mGetContent1.launch("image/*");
            });
            dialog.setNegativeButton("Camera", (dialogInterface, i) -> {
                pickCamera();
            });
            dialog.show();
        });

        mGetContent1 = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null){
                cropperActivity(result);
            }
        });

    }

    public void cropperActivity(Uri image_uri){
        if (image_uri != null){
            Intent intent = new Intent(activity, CropperActivity.class);
            intent.putExtra("IMG_URI", image_uri);
            startActivityForResult(intent, 10118);
        }
    }

    private void pickCamera() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            dest_uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, dest_uri);

            startActivityForResult(cameraIntent, 10119);

        }catch (Exception e){
            Log.e("errnos", e.toString());
        }
    }

    private void getTextFromImage(Bitmap selected_image_bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(activity).build();
        if (!recognizer.isOperational()){
            Toast.makeText(activity, "Text Recognizer is not working...", Toast.LENGTH_SHORT).show();
        }else {
            Frame frame = new Frame.Builder().setBitmap(selected_image_bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            Log.e("errnos", "\n"+ stringBuilder);
//            text_data.setText(stringBuilder.toString());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public  void imageCropped(Uri image_uri){
        if(image_uri == null){
            Toast.makeText(activity, "Something went wrong...", Toast.LENGTH_LONG).show();
            button_recrop.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image_crop_view.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_add_a_photo_24));
            }
        }else{
            button_recrop.setVisibility(View.VISIBLE);
            image_crop_view.setImageURI(image_uri);
            selected_image_uri = image_uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                button_next.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.primary)));
                image_crop_view.setImageTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == -1 && requestCode == 10118) {
                String result = data.getStringExtra("RESULT");
                if (result != null) {
                    imageCropped(Uri.parse(result));
                }
            } else if (resultCode == -1 && requestCode == 10119) {
                if (dest_uri != null){
                    cropperActivity(dest_uri);
                }
            }
        }catch (Exception e){
            Log.e("errnos", e.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(activity, "Running in background...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }
}