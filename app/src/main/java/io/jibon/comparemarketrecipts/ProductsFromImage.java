package io.jibon.comparemarketrecipts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProductsFromImage extends AppCompatActivity {
    protected ArrayList<ArrayList> arrayList;
    EditText editTextShopName;
    String allTextFromImage, shop_id, addedShopId, shop_country_name, shop_city_name, shop_name;
    Activity activity;
    ListView listViewShopNamesFromInternet, listViewForAddingItemsOnServer;
    ProgressBar editTextShopNameChangerLoading;
    RelativeLayout get_shop_name_rl_layout, add_shop_items_rl_layout;
    TextView shop_name_for_adding, shop_location_for_adding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_from_image);
        activity = this;


        editTextShopName = activity.findViewById(R.id.edit_text_shop_name);
        listViewShopNamesFromInternet = activity.findViewById(R.id.list_shop_names);
        get_shop_name_rl_layout = activity.findViewById(R.id.get_shop_name_rl_layout);
        add_shop_items_rl_layout = activity.findViewById(R.id.add_shop_items_rl_layout);
        editTextShopNameChangerLoading = activity.findViewById(R.id.editTextShopNameChangerLoading);
        shop_name_for_adding = activity.findViewById(R.id.shop_name_for_adding);
        shop_location_for_adding = activity.findViewById(R.id.shop_location_for_adding);
        listViewForAddingItemsOnServer = activity.findViewById(R.id.show_related_products_listview);

        get_shop_name_rl_layout.setVisibility(View.VISIBLE);
        editTextInputOnchange(" ", editTextShopNameChangerLoading);
        editTextShopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editTextInputOnchange(editable.toString(), editTextShopNameChangerLoading);
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            allTextFromImage = extras.getString("AllTextFromImage");
        }

        arrayList = new ArrayList<>();

        String[] allLinesFromImage = allTextFromImage.split("\n");
        for (int i = 0; i < allLinesFromImage.length; i++) {
            String product, price;
            if (allLinesFromImage[i].startsWith("$") || allLinesFromImage[i].startsWith("€")) {
                product = allLinesFromImage[i - 1];
                price = allLinesFromImage[i];
                if (product != null && price != null && !product.contains("TOTAL") && !product.contains("AMOUNT")) {
                    ArrayList arrayList1 = new ArrayList();
                    product = product.replaceAll("[^A-Za-z\\s]+", "").trim();
                    price = price.replaceAll("[^0-9\\.]+", "").trim();
                    arrayList1.add(product);
                    arrayList1.add(price);
                    arrayList.add(arrayList1);
                }
            }
        }

    }


    public void editTextInputOnchange(String editable, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        String link = new Settings(activity).linkForJson("api.php?getAllShopName=1&shop_name=" + editable.toString());
        Internet2 task = new Internet2(activity, link, (code, result) -> {
            try {
                progressBar.setVisibility(View.GONE);
                if (code == 200 && result != null) {
                    if (result.has("getAllShopName")) {
                        JSONArray getAllShopName = result.getJSONArray("getAllShopName");
                        String oneNewShopAddingButton = "{\"id\": \"0\",\n" +
                                "      \"country\": \"Country\",\n" +
                                "      \"city\": \"City\",\n" +
                                "      \"shop_name\": \"Add " + editable + "\",\n" +
                                "      \"time\": \"0\"}";
                        JSONObject jsonObject = new JSONObject(oneNewShopAddingButton);
                        getAllShopName.put(jsonObject);
                        ArrayList<String> shopNameArrayList = new ArrayList<>();
                        for (int index = 0; index < getAllShopName.length(); index++) {
                            shopNameArrayList.add(getAllShopName.getJSONObject(index).getString("shop_name"));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_2, android.R.id.text1, shopNameArrayList) {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                try {
                                    text1.setText(getAllShopName.getJSONObject(position).getString("shop_name"));
                                    text2.setText(getAllShopName.getJSONObject(position).getString("city") + ", " + getAllShopName.getJSONObject(position).getString("country"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                view.setOnClickListener(view1 -> {
                                    try {
                                        if (getAllShopName.getJSONObject(position).getInt("id") == 0) {
                                            if (editable.length() < 3) {
                                                new Settings(activity).toast("Please enter a valid name", null);
                                            } else {
                                                JSONObject countries_states = new Settings(activity).countries_states();
                                                if (countries_states.has("countries")) {
                                                    JSONArray countries = countries_states.getJSONArray("countries");
                                                    List<String> countries_names = new ArrayList<>();
                                                    for (int i = 0; i < countries.length(); i++) {
                                                        countries_names.add(countries.getJSONObject(i).getString("name"));
                                                    }
                                                    String[] countries_list = countries_names.toArray(new String[0]);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                                    builder.setTitle("Select shop country");
                                                    builder.setItems(countries_list, (dialog, which) -> {
                                                        try {
                                                            Integer shop_country_id = countries.getJSONObject(which).getInt("id");
                                                            JSONArray statesJSONArray = countries_states.getJSONArray("states");
                                                            List<String> states_names = new ArrayList<>();
                                                            for (int i = 0; i < statesJSONArray.length(); i++) {
                                                                if (statesJSONArray.getJSONObject(i).getInt("country_id") == shop_country_id) {
                                                                    states_names.add(statesJSONArray.getJSONObject(i).getString("name"));
                                                                }
                                                            }
                                                            String[] states_list = states_names.toArray(new String[0]);
                                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                                                            builder2.setTitle("Select shop city");
                                                            builder2.setItems(states_list, (dialog2, which2) -> {
                                                                try {
                                                                    progressBar.setVisibility(View.VISIBLE);
                                                                    String link = new Settings(activity).linkForJson("api.php?addShopName=1&shop_name=" + URLEncoder.encode(editable) + "&country=" + URLEncoder.encode(countries_names.get(which)) + "&city=" + URLEncoder.encode(states_names.get(which2)));
                                                                    Internet2 task = new Internet2(activity, link, (code, result) -> {
                                                                        progressBar.setVisibility(View.GONE);
                                                                        try {
                                                                            if (result.has("addShopName")) {
                                                                                shop_country_name = countries_names.get(which);
                                                                                shop_city_name = states_names.get(which2);
                                                                                shop_name = editable.toString();
                                                                                shop_id = result.getString("addShopName");
                                                                                new Settings(activity).toast(editable + " added succesfully", R.drawable.ic_baseline_done_24);
                                                                            } else {
                                                                                new Settings(activity).toast("Something went wrong\nPlease try again...", R.drawable.ic_baseline_clear_24);
                                                                            }
                                                                        } catch (Exception e) {
                                                                            Log.e("errnos", "country select error: " + e.toString());
                                                                            new Settings(activity).toast(editable + " was not added\nPlease try again...", R.drawable.ic_baseline_clear_24);
                                                                        }
                                                                    });
                                                                    task.execute();
                                                                } catch (Exception e) {
                                                                    Log.e("errnos", "country select error: " + e.toString());
                                                                }
                                                            });
                                                            builder2.setCancelable(false);
                                                            builder2.show();
                                                        } catch (Exception e) {
                                                            Log.e("errnos", "country select error: " + e.toString());
                                                        }
                                                    });
                                                    builder.setCancelable(false);
                                                    builder.show();
                                                }

                                            }
                                        } else {
                                            shop_name = getAllShopName.getJSONObject(position).getString("shop_name");
                                            shop_city_name = getAllShopName.getJSONObject(position).getString("city");
                                            shop_country_name = getAllShopName.getJSONObject(position).getString("country");
                                            shop_id = getAllShopName.getJSONObject(position).getString("id");
                                        }
                                        progressBar.setVisibility(View.VISIBLE);
                                        String link3 = new Settings(activity).linkForJson("api.php?addShopId=" + URLEncoder.encode(shop_id));
                                        Internet2 task3 = new Internet2(activity, link3, (code, result) -> {
                                            progressBar.setVisibility(View.GONE);
                                            try {
                                                if (result.has("addShopId")) {
                                                    shop_name_for_adding.setText(shop_name);
                                                    shop_location_for_adding.setText(shop_city_name + ", " + shop_country_name);
                                                    addedShopId = result.getString("addShopId");
                                                    get_shop_name_rl_layout.setVisibility(View.GONE);
                                                    add_shop_items_rl_layout.setVisibility(View.VISIBLE);
                                                    getSameDataListOfProductsNames(arrayList, listViewForAddingItemsOnServer, progressBar);

                                                } else {
                                                    new Settings(activity).toast("Something went wrong\nPlease try again...", R.drawable.ic_baseline_clear_24);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        task3.execute();
                                    } catch (Exception e) {
                                        Log.e("errnos", "view1 onclick: " + e);
                                    }
                                });
                                return view;
                            }
                        };
                        listViewShopNamesFromInternet.setAdapter(adapter);
                    }
                }
            } catch (Exception e) {
                Log.e("errnos", "shop_names " + e);
            }
        });
        task.execute();
    }

    private void getSameDataListOfProductsNames(ArrayList<ArrayList> product_name_price_list, ListView listViewForAddingItemsOnServer, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        StringBuilder procutsWithComma = new StringBuilder();
        for (int i = 0; i < product_name_price_list.size(); i++) {
            procutsWithComma.append(product_name_price_list.get(i).get(0)).append(",");
        }
        Log.e("errnos", procutsWithComma.toString());
        String link = new Settings(activity).linkForJson("api.php?getSimilar=" + URLEncoder.encode(procutsWithComma.toString()));
        Internet2 task = new Internet2(activity, link, (code, result) -> {
            try {
                progressBar.setVisibility(View.GONE);
                if (code == 200 && result != null) {
                    if (result.has("getSimilar")) {
                        BaseAdapter baseAdapter = new SameProductListAdapter(activity, product_name_price_list, result.getJSONObject("getSimilar"), shop_id, shop_name);
                        listViewForAddingItemsOnServer.setAdapter(baseAdapter);
                    }
                    Log.e("errnos", result.toString());
                }
            } catch (Exception e) {
                e.toString();
            }
        });
        task.execute();
    }

}