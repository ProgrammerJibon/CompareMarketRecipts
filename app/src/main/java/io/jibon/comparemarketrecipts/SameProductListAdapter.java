package io.jibon.comparemarketrecipts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class SameProductListAdapter extends BaseAdapter {
    public ArrayList<ArrayList> arrayList;
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
                convertView.setTag(playersViewHolder);
            } else {
                playersViewHolder = (PlayersViewHolder) convertView.getTag();
            }

            ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
            TextView product_name_edittext = playersViewHolder.product_name_edittext;
            GridView gridview_for_similar_data_types = playersViewHolder.gridview_for_similar_data_types;

            product_name_edittext.setText("Related to " + arrayList.get(position).get(0) + " (" + arrayList.get(position).get(1) + ")");

            if (resultFromServer.has(String.valueOf(arrayList.get(position).get(0)).toLowerCase())) {
                Boolean $show_add = true;
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
                ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.sample_similars_datatype, R.id.productName, stringArrayList);
                gridview_for_similar_data_types.setAdapter(arrayAdapter);
                View finalConvertView = convertView;
                gridview_for_similar_data_types.setOnItemClickListener((adapterView, view, i, l) -> {
                    view.setOnClickListener(null);
                    progressBar.setVisibility(View.VISIBLE);
                    String link = new Settings(activity).linkForJson("comparemarketrecipts.php?addProductItem=" + shop_id + "&productName=" + URLEncoder.encode(stringArrayList.get(i)) + "&productPrice=" + URLEncoder.encode(String.valueOf(arrayList.get(position).get(1))));
                    Internet2 task = new Internet2(activity, link, (code, result) -> {
                        try {
                            progressBar.setVisibility(View.GONE);
                            if (code == 200 && result != null) {
                                if (result.has("addProductItem")) {
                                    if (result.getInt("addProductItem") > 0) {
                                        finalConvertView.setAlpha(0.3F);
                                        new Settings(activity).toast(stringArrayList.get(i) + " added to " + shop_name + " for price comparing", R.drawable.ic_baseline_done_24);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("errnos", e.toString());
                        }
                    });
                    task.execute();
                });
            }


        } catch (Exception error) {
            Log.e("errnos_teamada", error.toString());
        }
        return convertView;
    }

    public class PlayersViewHolder {
        public TextView product_name_edittext = null;
        public GridView gridview_for_similar_data_types = null;
    }
}