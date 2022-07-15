package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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

        ArrayList<String[]> arrayList = new ArrayList<>();

        String[] allLinesFromImage = allTextFromImage.split("\n");
        for (int i = 0; i < allLinesFromImage.length; i++) {
            String product, price;
            if (allLinesFromImage[i].startsWith("$") || allLinesFromImage[i].startsWith("€")) {
                product = allLinesFromImage[i - 1];
                price = allLinesFromImage[i];
                if (product != null && price != null && !product.contains("TOTAL") && !product.contains("AMOUNT")) {
                    product = product.replaceAll("[^A-Za-z\\s]+", "");
                    price = price.replaceAll("[^0-9\\.]+", "");
                    Log.e("errnos", product + "\t" + price);
                }
            }
        }
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Internet2 task = new Internet2(activity, "https://reqres.in/api/products/3", (code, result) -> {
            if (code == 200) {
                Log.e("errnos", String.valueOf(result));
            }
        });
        task.execute();
        tv.setText(allTextFromImage);

    }
}