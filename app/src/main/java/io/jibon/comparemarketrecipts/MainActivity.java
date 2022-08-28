package io.jibon.comparemarketrecipts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import io.jibon.comparemarketrecipts.Fragments.AddByImage;
import io.jibon.comparemarketrecipts.Fragments.ShowPrices;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    public TabLayout tabLayoutMainActivity;
    public ViewPager2 viewPager2;
    public Integer pageNumber = -1;
    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    private LinearLayout adViewContainer;
    int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find blocks
        tabLayoutMainActivity = activity.findViewById(R.id.home_tab_layout);
        viewPager2 = activity.findViewById(R.id.home_view_pager);
        adViewContainer = activity.findViewById(R.id.adView1);
        String AD_UNIT_ID = "";
        if (BuildConfig.DEBUG) {
            AD_UNIT_ID = ("ca-app-pub-3940256099942544/6300978111");//test ad unit id
        } else {
            AD_UNIT_ID = ("ca-app-pub-6695709429891253/5553013652");//my ad unit id
        }
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.FULL_BANNER);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("errnos", loadAdError.getMessage());
            }
        });
        mAdView.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        MobileAds.initialize(this, initializationStatus -> {

        });
        adViewContainer.removeAllViews();
        adViewContainer.removeAllViewsInLayout();
        adViewContainer.addView(mAdView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pageNumber = extras.getInt("pageNumber");
        }


        Bitmap bitmap1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo);



        run();

        String installTime = null, updateTime = null;

        try {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            installTime = dateFormat.format(new Date(pi.firstInstallTime));
            updateTime = dateFormat.format(new Date(pi.lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String finalInstallTime = installTime;
        String finalUpdateTime = updateTime;
        activity.findViewById(R.id.app_info).setOnClickListener(view -> {
            String html = activity.getString(R.string.app_name) + " is an open AI platform which allows users to upload their market receipts to our server and add the products name and their prices from the receipt to the Market Store page." +
                    "<br><br>This app will allow other users to see the prices by searching their <i>country</i> and <i>city</i> name." +
                    "<br><br>This app will help others by adding your bought details and other user will determine the current price of their shop and can go for shopping mind freely with their budget. " +
                    "<br><small>" +
                    "<br>App Name: " + activity.getString(R.string.app_name) +
                    "<br>App Version Name: " + BuildConfig.VERSION_NAME +
                    "<br>App Version Code: " + BuildConfig.VERSION_CODE +
                    "<br>Install Time: " + finalInstallTime +
                    "<br>Update Time: " + finalUpdateTime +
                    "<br>APK Name: " + activity.getPackageName() +
                    "<br><i>" +
                    "<br>Powered and owned by: <a href='#'>Giovanni Presti</a>" +
                    "<br>Contact: <a href='tel:+3914000000'>+3914000000</a>, " +
                    "<a href='mailto:info@miorispermio.com'>info@miorispermio.com</a>" +
                    "<br>Web: <a href='https://miorispermio.com/'>www.miorispermio.com</a>" +
                    "<br><br>Developed by: <a href='https://www.freelancer.com/u/ProgrammerJibon'>MD. Jibon Howlader</a> " + "(ProgrammerJibon)" +
                    "<br>Contact: " +
                    "<a href='https://www.freelancer.com/u/ProgrammerJibon'>Freelancer</a>, " +
                    "<a href='https://www.instagram.com/programmerjibon/'>Instagram</a>, " +
                    "<a href='https://www.linkedin.com/in/programmerjibon/'>Linkedin</a>, " +
                    "<a href='mailto:mail@jibon.io'>Email</a>" +
                    "<br>Website: <a href='https://jibon.io/'>www.jibon.io</a> & <a href='https://www.jibon.io/LICENSE'>LICENSE</a>" +
                    "<br>Google Verified Developer Id: <a href='https://g.dev/ProgrammerJibon'>GDEV#ProgrammerJibon</a>" +
                    "</i></small><br><br>";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("About App")
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .setMessage(Html.fromHtml(html))
                    .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
            ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            ImageView imageView = dialog.findViewById(android.R.id.icon);
            if (imageView != null) {
                imageView.setColorFilter(Color.parseColor("#e0e0e0"), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageNumber != -1 && viewPager2.getChildCount() > pageNumber) {
            viewPager2.setCurrentItem(pageNumber);
            pageNumber = -1;
        }
    }

    @SuppressLint("MissingPermission")
    public void run() {
        if (hasPermission(this, permissions)) {
            try {


                tabLayoutMainActivity.addTab(tabLayoutMainActivity.newTab().setIcon((int) R.drawable.ic_baseline_add_24));
                tabLayoutMainActivity.addTab(tabLayoutMainActivity.newTab().setIcon((int) R.drawable.ic_baseline_location_city_24));
                tabLayoutMainActivity.addTab(tabLayoutMainActivity.newTab().setIcon((int) R.drawable.ic_baseline_person_24));
                ArrayList<Fragment> fragments = new ArrayList<>();
                fragments.add(new AddByImage());
                fragments.add(new ShowPrices(false));
                fragments.add(new ShowPrices(true));
                viewPager2.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        if (((double) positionOffset) == 0.0d) {
                            tabLayoutMainActivity.selectTab(tabLayoutMainActivity.getTabAt(position));
                        }
                        tabLayoutMainActivity.setScrollPosition(position, positionOffset, false);
                    }
                });
                viewPager2.setCurrentItem(1, true);
                this.tabLayoutMainActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager2.setCurrentItem(tab.getPosition(), true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Objects.requireNonNull(tab.getIcon()).setTint(activity.getColor(R.color.secondary));
                        }
                    }

                    public void onTabUnselected(TabLayout.Tab tab) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Objects.requireNonNull(tab.getIcon()).setTint(activity.getColor(R.color.gray));
                        }
                    }

                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });
            } catch (Exception error) {
                Log.e("errnos", error.toString());
            }
        } else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    public boolean hasPermission(Context context, String[] permissionx) {
        if (context != null && permissionx != null) {
            for (String permission : permissionx) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean results = true, need_settings = false;
        int i = 0;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])){
                    need_settings = true;
                }
                results = false;
                break;
            }
            i++;
        }
        if (need_settings){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Permission needed!");
            builder.setMessage("App need permission to read contacts");
            builder.setPositiveButton("CustomTools", (dialogInterface, i1) -> {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                finish();
            });
            builder.setNegativeButton("Exit", (dialogInterface, i1) -> {
                activity.finish();
            });
            builder.show();
        } else if (results) {
            run();
        } else {
            finish();
        }
    }

    public static class FragmentAdapter extends FragmentStateAdapter {
        ArrayList<Fragment> fragments;

        public FragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, ArrayList<Fragment> fragments2) {
            super(fragmentManager, lifecycle);
            this.fragments = fragments2;
        }

        public Fragment createFragment(int position) {
            return this.fragments.get(position);
        }

        public int getItemCount() {
            return this.fragments.size();
        }
    }
}