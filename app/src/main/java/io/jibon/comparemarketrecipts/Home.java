package io.jibon.comparemarketrecipts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class Home extends AppCompatActivity {
    Button button_next;
    TextView text_data;
    Activity activity;
    ImageView image_crop_view;
    ActivityResultLauncher<String> mGetContent1;
    ActivityResultLauncher<Uri> mGetContent2;
    Uri selected_image_uri, dest_uri;
    Bitmap selected_image_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;

        image_crop_view = activity.findViewById(R.id.image_view_crop);
        button_next = activity.findViewById(R.id.button_next);
        text_data = activity.findViewById(R.id.text_data);




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
            mGetContent1.launch("image/*");
//            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
//            dialog.setTitle("Choose an option");
//            dialog.setPositiveButton("Gallery", (dialogInterface, i) -> {
//                mGetContent1.launch("image/*");
//            });
//            dialog.setNegativeButton("Camera", (dialogInterface, i) -> {
//                pickCamera();
//            });
//            dialog.show();
        });

        mGetContent1 = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null){
                Intent intent = new Intent(activity, CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 10118);
            }
        });

    }

    public void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        dest_uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, dest_uri);

        startActivityForResult(cameraIntent, 10118); // OLD WAY


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
                stringBuilder.append("\\n");
            }
            text_data.setText(stringBuilder.toString());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 10118){
            String result = data.getStringExtra("RESULT");
            Uri resultUri;
            if (result != null){
                resultUri = Uri.parse(result);
                image_crop_view.setImageURI(resultUri);
                selected_image_uri = resultUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    button_next.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.primary)));
                    image_crop_view.setImageTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                }
            }
        }
    }
}