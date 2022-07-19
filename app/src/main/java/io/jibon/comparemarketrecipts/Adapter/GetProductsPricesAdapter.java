package io.jibon.comparemarketrecipts.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.jibon.comparemarketrecipts.R;

public class GetProductsPricesAdapter extends BaseAdapter {
    Activity activity;
    JSONArray productsPrices;

    public GetProductsPricesAdapter(Activity activity, JSONArray getProductsPrices) {
        this.activity = activity;
        this.productsPrices = getProductsPrices;
    }

    @Override
    public int getCount() {
        return productsPrices.length();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        try {
            ItemsViewHolder itemsViewHolder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.sample_get_products_prices_adapter, viewGroup, false);
                itemsViewHolder = new ItemsViewHolder();
                itemsViewHolder.productName = convertView.findViewById(R.id.productName);
                itemsViewHolder.productPrice = convertView.findViewById(R.id.productPrice);
                itemsViewHolder.shopName = convertView.findViewById(R.id.shopName);
                itemsViewHolder.setOfDate = convertView.findViewById(R.id.date);
                itemsViewHolder.setOfTime = convertView.findViewById(R.id.time);
                itemsViewHolder.shopLocation = convertView.findViewById(R.id.shopLocation);

                convertView.setTag(itemsViewHolder);
            } else {
                itemsViewHolder = (ItemsViewHolder) convertView.getTag();
            }

            TextView productName = itemsViewHolder.productName,
                    productsPrice = itemsViewHolder.productPrice,
                    shopName = itemsViewHolder.shopName,
                    date = itemsViewHolder.setOfDate,
                    time = itemsViewHolder.setOfTime,
                    shopLocation = itemsViewHolder.shopLocation;
            productName.setText(((JSONObject) productsPrices.get(position)).getString("item_name"));
            productsPrice.setText(((JSONObject) productsPrices.get(position)).getString("item_price"));
            shopName.setText(((JSONObject) productsPrices.get(position)).getString("shop_name"));
            shopLocation.setText(((JSONObject) productsPrices.get(position)).getString("city") + ", " + ((JSONObject) productsPrices.get(position)).getString("country"));
            String timeUniversal = ((JSONObject) productsPrices.get(position)).getString("time") + "000";

            if (((JSONObject) productsPrices.get(position)).has("byYou")) {
                if (((JSONObject) productsPrices.get(position)).getBoolean("byYou")) {
                    convertView.setAlpha(0.5F);
                } else {
                    convertView.setAlpha(1F);
                }
            }

            long yourmilliseconds = Long.parseLong(timeUniversal);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(yourmilliseconds);

            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss aa");

            date.setText(sdf.format(resultdate));
            time.setText(stf.format(resultdate));

        } catch (Exception e) {
            Log.e("errnos", e.toString());
        }
        return convertView;
    }


    public static class ItemsViewHolder {
        public TextView productName, productPrice, shopName, setOfDate, shopLocation, setOfTime;
    }
}
