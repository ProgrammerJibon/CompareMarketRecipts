package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductsFromImage extends AppCompatActivity {
    String allTextFromImage;
    TextView tv;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_from_image);
        activity = this;

        tv = activity.findViewById(R.id.tv);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            allTextFromImage = extras.getString("AllTextFromImage");
        }

        tv.setText(allTextFromImage);

    }
}