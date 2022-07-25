package io.jibon.comparemarketrecipts.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;

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
//                itemsViewHolder.adView2 = convertView.findViewById(R.id.adView2);
                itemsViewHolder.templateView = convertView.findViewById(R.id.my_template);
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
//            AdView mAdView2 = itemsViewHolder.adView2;
            TemplateView templateView = itemsViewHolder.templateView;

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
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(yourmilliseconds);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss aa");

            date.setText(sdf.format(resultdate));
            time.setText(stf.format(resultdate));


            if (position % 7 == 0) {

                MobileAds.initialize(activity);
                AdLoader adLoader = new AdLoader.Builder(activity, "ca-app-pub-6695709429891253/9897159758") //ca-app-pub-3940256099942544/2247696110
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(new ColorDrawable(activity.getColor(R.color.white)))
                                        .build();
                                templateView.setStyles(styles);
                                templateView.setNativeAd(nativeAd);
                            }

                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                templateView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                templateView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                templateView.setVisibility(View.GONE);
                                Log.e("errnos addError", adError.getMessage());
                            }
                        })
                        .build();

                adLoader.loadAds(new AdRequest.Builder().build(), 5);

            }



//            if (position % 10 == 0) {
//                mAdView2.setVisibility(View.VISIBLE);
//                MobileAds.initialize(activity, initializationStatus -> {
//
//                });
//
//                AdRequest adRequest = new AdRequest.Builder().build();
//                mAdView2.loadAd(adRequest);
//            } else {
//                mAdView2.setVisibility(View.GONE);
//            }

        } catch (Exception e) {
            Log.e("errnos", e.toString());
        }
        return convertView;
    }


    public static class ItemsViewHolder {
        public TextView productName, productPrice, shopName, setOfDate, shopLocation, setOfTime;
        private AdView adView2;
        private TemplateView templateView;
    }

}
