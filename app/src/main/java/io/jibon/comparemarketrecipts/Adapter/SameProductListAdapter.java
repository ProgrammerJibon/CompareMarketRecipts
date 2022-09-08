package io.jibon.comparemarketrecipts.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.jibon.comparemarketrecipts.CustomTools;
import io.jibon.comparemarketrecipts.R;

public class SameProductListAdapter extends BaseAdapter {
    public ArrayList<ArrayList> arrayList;
    public ArrayList<Button> add_button_list;
    public Activity activity;
    public String user_role, shop_id, shop_name;
    public JSONObject resultFromServer;

    public SameProductListAdapter(Activity activity, ArrayList<ArrayList> arrayList, JSONObject resultFromServer, String shop_id, String shop_name) {
        this.arrayList = arrayList;
        this.activity = activity;
        this.user_role = user_role;
        this.resultFromServer = resultFromServer;
        this.shop_id = shop_id;
        this.shop_name = shop_name;
        this.add_button_list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        try {
            return position;
        } catch (Exception e) {
            e.printStackTrace();
            return Long.parseLong(("0." + position));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            PlayersViewHolder playersViewHolder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.sample_products_same_data, parent, false);
                playersViewHolder = new PlayersViewHolder();
                playersViewHolder.product_name_edittext = convertView.findViewById(R.id.product_name_edittext);
                playersViewHolder.gridview_for_similar_data_types = convertView.findViewById(R.id.gridview_for_similar_data_types);
                playersViewHolder.gridview_for_similar_data_types_price = convertView.findViewById(R.id.gridview_for_similar_data_types_price);
                playersViewHolder.gridview_for_similar_data_types_price_add_button = convertView.findViewById(R.id.gridview_for_similar_data_types_price_add_button);

                add_button_to_button_list(playersViewHolder.gridview_for_similar_data_types_price_add_button);
                convertView.setTag(playersViewHolder);
            } else {
                playersViewHolder = (PlayersViewHolder) convertView.getTag();
            }


            ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
            TextView product_name_edittext = playersViewHolder.product_name_edittext;
            Spinner gridview_for_similar_data_types = playersViewHolder.gridview_for_similar_data_types;
            EditText gridview_for_similar_data_types_price = playersViewHolder.gridview_for_similar_data_types_price;
            Button gridview_for_similar_data_types_price_add_button = playersViewHolder.gridview_for_similar_data_types_price_add_button;

            product_name_edittext.setText("Related to " + arrayList.get(position).get(0) + " (" + arrayList.get(position).get(1) + ")");
            gridview_for_similar_data_types_price.setText(arrayList.get(position).get(1)+"");

            if (resultFromServer.has(String.valueOf(arrayList.get(position).get(0)).toLowerCase())) {
                boolean $show_add = true;
                ArrayList<String> stringArrayList = new ArrayList<>();
                JSONArray $resultFromServer = resultFromServer.getJSONArray(String.valueOf(arrayList.get(position).get(0)).toLowerCase());
                for (int i = 0; i < $resultFromServer.length(); i++) {
                    if (String.valueOf(arrayList.get(position).get(0)).toLowerCase().equals($resultFromServer.getJSONObject(i).getString("item_name"))) {
                        $show_add = false;
                    }
                    stringArrayList.add($resultFromServer.getJSONObject(i).getString("item_name"));
                }
                if ($show_add) {
                    stringArrayList.add(("" + arrayList.get(position).get(0)).toLowerCase());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, android.R.id.text1, stringArrayList){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.GRAY);
                        return view;
                    }
                };
                gridview_for_similar_data_types.setAdapter(arrayAdapter);

                gridview_for_similar_data_types.setSelection(stringArrayList.size() - 1, true);

                View finalConvertView = convertView;
                gridview_for_similar_data_types_price_add_button.setOnClickListener(view -> {
                    try {
                        gridview_for_similar_data_types_price_add_button.setOnClickListener(null);
                        gridview_for_similar_data_types_price_add_button.setClickable(false);
                        gridview_for_similar_data_types_price_add_button.setEnabled(false);
                        gridview_for_similar_data_types_price.setFocusable(false);
                        gridview_for_similar_data_types.setClickable(false);
                        gridview_for_similar_data_types.setEnabled(false);
                        new CustomTools(activity).addItemsPricesOfCity(progressBar, shop_id, stringArrayList.get(gridview_for_similar_data_types.getSelectedItemPosition()), String.valueOf(gridview_for_similar_data_types_price.getText()), shop_name, finalConvertView);
                    } catch (Exception error) {
                        Log.e("errnos add button cliker", error.toString());
                    }
                });

            }


        } catch (Exception error) {
            Log.e("errnos_teamada", error.toString());
        }
        return convertView;
    }

    public ArrayList<Button> getAdd_button_list() {
        return add_button_list;
    }

    public void add_button_to_button_list(Button button) {
        this.add_button_list.add(button);
    }

    public static class PlayersViewHolder {
        public TextView product_name_edittext = null;
        public Spinner gridview_for_similar_data_types = null;
        public EditText gridview_for_similar_data_types_price = null;
        public Button gridview_for_similar_data_types_price_add_button = null;
        private String products_name, products_price;

    }
}