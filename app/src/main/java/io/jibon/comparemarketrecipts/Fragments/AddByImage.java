package io.jibon.comparemarketrecipts.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

import io.jibon.comparemarketrecipts.CropperActivity;
import io.jibon.comparemarketrecipts.CustomTools;
import io.jibon.comparemarketrecipts.ProductsFromImage;
import io.jibon.comparemarketrecipts.R;

public class AddByImage extends Fragment {

    Button button_next, button_recrop, goForCropOkayButton;
    Activity activity;
    ImageView get_image_from_gallery_or_camera;
    ActivityResultLauncher<String> mGetContent1;
    Uri selected_image_uri, dest_uri;
    Bitmap selected_image_bitmap;
    TextView mainActivityTitle;
    String pageTitle;
    Boolean periodForDecimal = true;
    RelativeLayout show_how_to_crop;

    ArrayList<String> productsXXX = new ArrayList();
    ArrayList<String> priceXXX = new ArrayList();


    public AddByImage() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            mainActivityTitle.setText(pageTitle);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Activity context) {
        super.onAttach(context);
        this.activity = context;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragments = inflater.inflate(R.layout.fragment_home, container, false);
        // find blocks
        mainActivityTitle = activity.findViewById(R.id.mainActivityTitle);
        get_image_from_gallery_or_camera = viewFragments.findViewById(R.id.get_image_from_gallery_or_camera);
        button_next = viewFragments.findViewById(R.id.button_next);
        button_recrop = viewFragments.findViewById(R.id.button_recrop);
        goForCropOkayButton = viewFragments.findViewById(R.id.goForCropOkayButton);
        show_how_to_crop = viewFragments.findViewById(R.id.show_how_to_crop);

        // set startup values
        pageTitle = ("Scan receipts");

        // start

        button_recrop.setOnClickListener(view -> {
            if (selected_image_uri != null) {
                cropperActivity(selected_image_uri);
            }
        });

        button_next.setOnClickListener(view -> {
            if (selected_image_uri == null) {
                Toast.makeText(activity, "Please select an image first...", Toast.LENGTH_LONG).show();
            }else{
                try {
                    selected_image_bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selected_image_uri);
                    String textFromImage = "";
                    priceXXX = new ArrayList<>(); productsXXX = new ArrayList<>();
                    if (getTextFromImage(selected_image_bitmap)) {
                        for (int priceIndex = 0; priceIndex < productsXXX.size(); priceIndex++) {
                            if (priceIndex >= productsXXX.size()) {
                                continue;
                            }
                            if (priceIndex < priceXXX.size()) {
                                textFromImage += productsXXX.get(priceIndex) + "\n$" + priceXXX.get(priceIndex) + "\n";
                            }
                        }
                    }

//                    Log.e("errnos",productsXXX.toString());
//                    Log.e("errnos",textFromImage);
                    if (!textFromImage.equals("")) {
                        Intent intent = new Intent(activity, ProductsFromImage.class);
                        intent.putExtra("AllTextFromImage", textFromImage);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e("errnos intent by add by image", e.toString());
                }
            }
        });

        get_image_from_gallery_or_camera.setOnClickListener(view -> {
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
        return viewFragments;
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

        } catch (Exception e) {
            Log.e("errnos", e.toString());
        }
    }

    public void cropperActivity(Uri image_uri) {
        if (image_uri != null) {
            show_how_to_crop.setVisibility(View.VISIBLE);
            goForCropOkayButton.setOnClickListener(view -> {
                show_how_to_crop.setVisibility(View.GONE);
                Intent intent = new Intent(activity, CropperActivity.class);
                intent.putExtra("IMG_URI", image_uri);
                startActivityForResult(intent, 10118);
            });
        }
    }

    private Boolean getTextFromImage(Bitmap selected_image_bitmap) {
        Boolean result = false;
        TextRecognizer recognizer = new TextRecognizer.Builder(activity).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(activity, "Text Recognizer is not working...", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(selected_image_bitmap).build();
            SparseArray<TextBlock> textBlocks = recognizer.detect(frame);
            String blocks = "";
            String lines = "";
            String words = "";
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                blocks = blocks + tBlock.getValue() + "\n\n";
                for (Text line : tBlock.getComponents()) {
                    //extract scanned text lines here
                    if (!line.getValue().endsWith("%") && !line.getValue().startsWith("j ")) {
                        lines = lines + line.getValue() + "\n";
                    }
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        if (!element.getValue().endsWith("%")) {
                            words = words + element.getValue() + " ";
                        }
                    }
                    words = words + "\n";
                }
            }
            lines = lines.replace("$", "");
            lines = lines.replace("€", "");
            String products = lines.toLowerCase();
            String prices = lines.toLowerCase();

            String[] productsX = products.split("desc");
            if (productsX.length > 1) {
                products = "desc"+productsX[1];
            } else {
                productsX = products.split("item");
                if (productsX.length > 1) {
                    products = "item"+productsX[1];
                } else {
                    productsX = products.split("name");
                    if (productsX.length > 1) {
                        products = "name"+productsX[1];
                    }else {
                        productsX = products.split("deco");
                        if (productsX.length > 1) {
                            products = "deco"+productsX[1];
                        }
                    }
                }
            }
            productsX = products.split("subtot");
            if (productsX.length > 1) {
                products = productsX[0];
            } else {
                productsX = products.split("total");
                if (productsX.length > 1) {
                    products = productsX[0];
                } else {
                    productsX = products.split("sum");
                    if (productsX.length > 1) {
                        products = productsX[0];
                    } else {
                        productsX = products.split("vat");
                        if (productsX.length > 1) {
                            products = productsX[0];
                        } else {
                            productsX = products.split("iva");
                            if (productsX.length > 1) {
                                products = productsX[0];
                            } else {
                                productsX = products.split("prezzo");
                                if (productsX.length > 1) {
                                    products = productsX[0];
                                } else {
                                    productsX = products.split("price");
                                    if (productsX.length > 1) {
                                        products = productsX[0];
                                    } else {
                                        productsX = products.split("ammoun");
                                        if (productsX.length > 1) {
                                            products = productsX[0];
                                        }else {
                                            productsX = products.split("eur");
                                            if (productsX.length > 1) {
                                                products = productsX[0];
                                            }else {
                                                productsX = products.split("usd");
                                                if (productsX.length > 1) {
                                                    products = productsX[0];
                                                }else {
                                                    productsX = products.split("sub tot");
                                                    if (productsX.length > 1) {
                                                        products = productsX[0];
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }



            String[] productsXX = products.split("\n");
            boolean skip = true;
            for (String s : productsXX) {
                s = s.replaceAll(" +", " ");
                String sn = s.replaceAll(" +", "");
                sn = sn.replace("j ", "");
                sn = sn.replace("j", "");
                sn = sn.replace("x", "");
                if (periodForDecimal) {
                    sn = sn.replace(",", "");
                } else {
                    sn = sn.replace(".", "");
                    sn = sn.replace(",", ".");
                }

                s = s.replaceAll("[^A-Za-z\\s\\d]+", "");
                if (!isNumeric(sn) && !s.contains("cad ") && !s.contains("iva") && !s.contains("prezzo") && s.length() > 3) {
                    if (skip) {
                        if (s.startsWith("desc") || s.startsWith("name") || s.startsWith("item") || s.startsWith("deco")){
                            continue;
                        }
                        skip = false;
                    }
                    productsXXX.add(s);
                }

            }


            Log.e("errnos products name", productsXXX.toString());


            String[] pricesX = prices.split("pric");
            if (pricesX.length > 1) {
                prices = pricesX[1];
            } else {
                pricesX = prices.split("prez");
                if (pricesX.length > 1) {
                    prices = pricesX[1];
                } else {
                    pricesX = prices.split("ammoun");
                    if (pricesX.length > 1) {
                        prices = pricesX[1];
                    } else {
                        pricesX = prices.split("cost");
                        if (pricesX.length > 1) {
                            prices = pricesX[1];
                        }
                    }
                }
            }


            String[] pricesC = prices.split("\n");
            for (String s : pricesC) {
                if (periodForDecimal) {
                    s = s.replace(",", "");
                } else {
                    s = s.replace(".", "");
                    s = s.replace(",", ".");
                }
                s = s.replaceAll(" +", "");
                if (!s.endsWith("%") && isNumeric(s)) {
                    if (Double.parseDouble(s) < 999.99 && !isInteger(s) && Double.parseDouble(s) != 10.00 && Double.parseDouble(s) != 22.00) {
                        priceXXX.add(s);
                    }
                }
            }

            Log.e("errnos products prices", priceXXX.toString());

            result = true;


        }
        return result;
    }

    public  boolean isInteger(String str){
        try {
            return Integer.parseInt(str) > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public  void imageCropped(Uri image_uri) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose one");
        builder.setMessage("What is equivalent of decimal in the picture?");
        builder.setPositiveButton("Comma", (dialogInterface, i) -> {
            periodForDecimal = false;
            new CustomTools(activity).toast("Comma is set as decimal point for this image", null);
        });
        builder.setNegativeButton("Period", (dialogInterface, i) -> {
            periodForDecimal = true;
            new CustomTools(activity).toast("Period or Dot is set as decimal point for this image", null);
        });
        builder.setCancelable(false);
        builder.create().show();
        if (image_uri == null) {
            Toast.makeText(activity, "Something went wrong...", Toast.LENGTH_LONG).show();
            button_recrop.setVisibility(View.GONE);
            get_image_from_gallery_or_camera.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_add_a_photo_24));
        } else {
            button_recrop.setVisibility(View.VISIBLE);
            get_image_from_gallery_or_camera.setImageURI(image_uri);
            selected_image_uri = image_uri;
            button_next.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.primary)));
            get_image_from_gallery_or_camera.setImageTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == -1 && requestCode == 10118) {
                assert data != null;
                String result = data.getStringExtra("RESULT");
                if (result != null && !(result.equals(""))) {
                    if (Uri.parse(result) != null) {
                        imageCropped(Uri.parse(result));
                    }
                }
            } else if (resultCode == -1 && requestCode == 10119) {
                if (dest_uri != null){
                    cropperActivity(dest_uri);
                }
            }else if (resultCode == -2 && requestCode == 10118) {
                assert data != null;
                Bundle resultExtras = data.getExtras();
                if (resultExtras != null){
                    String result = resultExtras.getString("RESULTX");
                    imageCropped(Uri.parse(result));
                }
            }
        }catch (Exception e){
            Log.e("errnos", e.getMessage());
        }
    }
}