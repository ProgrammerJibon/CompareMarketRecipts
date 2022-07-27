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

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
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
                itemsViewHolder = new ItemsViewHolder(activity);
                itemsViewHolder.setShowAds(((JSONObject) productsPrices.get(position)).getBoolean("showAds"));
                itemsViewHolder.setProductName(convertView.findViewById(R.id.productName));
                itemsViewHolder.setProductPrice(convertView.findViewById(R.id.productPrice));
                itemsViewHolder.setShopName(convertView.findViewById(R.id.shopName));
                itemsViewHolder.setSetOfDate(convertView.findViewById(R.id.date));
                itemsViewHolder.setSetOfTime(convertView.findViewById(R.id.time));
//                itemsViewHolder.adView2 = convertView.findViewById(R.id.adView2);
                itemsViewHolder.setTemplateView(convertView.findViewById(R.id.my_template));
                itemsViewHolder.setShopLocation(convertView.findViewById(R.id.shopLocation));
                itemsViewHolder.loadTheAd();
                convertView.setTag(itemsViewHolder);
            } else {
                itemsViewHolder = (ItemsViewHolder) convertView.getTag();
            }

            TextView productName = itemsViewHolder.getProductName(),
                    productsPrice = itemsViewHolder.getProductPrice(),
                    shopName = itemsViewHolder.getShopName(),
                    date = itemsViewHolder.getSetOfDate(),
                    time = itemsViewHolder.getSetOfTime(),
                    shopLocation = itemsViewHolder.getShopLocation();

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
        private final Activity activity;
        public TextView productName, productPrice, shopName, setOfDate, shopLocation, setOfTime;
        public TemplateView templateView;
        public boolean showAds;

        public ItemsViewHolder(Activity activity) {
            this.activity = activity;
        }

        public TextView getProductName() {
            return productName;
        }

        public void setProductName(TextView productName) {
            this.productName = productName;
        }

        public TextView getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(TextView productPrice) {
            this.productPrice = productPrice;
        }

        public TextView getShopName() {
            return shopName;
        }

        public void setShopName(TextView shopName) {
            this.shopName = shopName;
        }

        public TextView getSetOfDate() {
            return setOfDate;
        }

        public void setSetOfDate(TextView setOfDate) {
            this.setOfDate = setOfDate;
        }

        public TextView getShopLocation() {
            return shopLocation;
        }

        public void setShopLocation(TextView shopLocation) {
            this.shopLocation = shopLocation;
        }

        public TextView getSetOfTime() {
            return setOfTime;
        }

        public void setSetOfTime(TextView setOfTime) {
            this.setOfTime = setOfTime;
        }

        public TemplateView getTemplateView() {
            return templateView;
        }

        public void setTemplateView(TemplateView templateView) {
            this.templateView = templateView;
        }

        public boolean isShowAds() {
            return showAds;
        }

        public void setShowAds(boolean showAds) {
            this.showAds = showAds;
        }

        public Activity getActivity() {
            return activity;
        }

        public void loadTheAd() {
            if (isShowAds()) {

                MobileAds.initialize(activity);
                String adUnitId = "";
                if (BuildConfig.DEBUG) {
                    adUnitId = "ca-app-pub-3940256099942544/2247696110"; //test ad unit id
                } else {
                    adUnitId = "ca-app-pub-6695709429891253/9897159758"; //my ad unit id
                }
                AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(new ColorDrawable(activity.getColor(R.color.white)))
                                        .build();
                                getTemplateView().setStyles(styles);
                                getTemplateView().setNativeAd(nativeAd);
                            }

                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                getTemplateView().setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                Log.e("errnos addError", adError.getMessage());
                            }
                        })
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());

            } else {
                getTemplateView().setVisibility(View.GONE);
            }
        }
    }

}
