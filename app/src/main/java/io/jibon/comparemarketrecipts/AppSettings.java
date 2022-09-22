package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

//        1. swipe to refresh (X)
//        2. save comma on settings page (X)
//        3. cropping demo only for one time (X)
//        4. UI change
//        5. save country and city (X)
public class AppSettings extends AppCompatActivity {

    LinearLayout app_info, app_exit;
    Activity activity;
    Spinner countrySelector, citySelector;
    RadioButton deci_equiv_dot, deci_equiv_comma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        activity = this;
        // find view
        app_info = activity.findViewById(R.id.app_details);
        countrySelector = activity.findViewById(R.id.countrySelector);
        citySelector = activity.findViewById(R.id.citySelector);
        deci_equiv_dot = activity.findViewById(R.id.deci_equiv_dot);
        deci_equiv_comma = activity.findViewById(R.id.deci_equiv_comma);
        app_exit = activity.findViewById(R.id.app_exit);

        // start of functioning
        app_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Close & Exit App")
                        .setMessage("Are you sure want to exit?\nAll Process will be closed and close the app too.")
                        .setPositiveButton("Exit", (dialogInterface, i) -> {
                            Toast.makeText(activity, "Thanks for using our app :)", Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                            activity.finishAffinity();
                            finishAndRemoveTask();
                            System.exit(1);
                            finish();
                        })
                        .setIcon(R.drawable.ic_baseline_exit_to_app_24)
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                        .setCancelable(true);
                builder.create();
                builder.show();
            }
        });
        String deci_equiv = new CustomTools(activity).setPref("deci_equiv", null);
        if (Objects.equals(deci_equiv, "dot")) {
            deci_equiv_dot.setChecked(true);
        } else if (Objects.equals(deci_equiv, "comma")) {
            deci_equiv_comma.setChecked(true);
        } else {
            new CustomTools(activity).setPref("deci_equiv", "dot");
            deci_equiv_dot.setChecked(true);
        }
        deci_equiv_dot.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                new CustomTools(activity).setPref("deci_equiv", "dot");
            }
        });
        deci_equiv_comma.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                new CustomTools(activity).setPref("deci_equiv", "comma");
            }
        });

        try {
            JSONObject countries_states = new CustomTools(activity).countries_states();
            if (countries_states.has("countries")) {
                JSONArray countries = countries_states.getJSONArray("countries");
                ArrayList<String> countries_names = new ArrayList<>();
                countries_names.add("All Countries");
                for (int i = 0; i < countries.length(); i++) {
                    countries_names.add(countries.getJSONObject(i).getString("name"));
                }
                ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, countries_names);
                countrySelector.setAdapter(countryArrayAdapter);

                countrySelector.setSelection(new CustomTools(activity).setPrefId("country_id", null));

                countrySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        try {
                            int shop_country_id = -1;
                            if (position != 0) {
                                new CustomTools(activity).setPrefId("country_id", position);
                                new CustomTools(activity).setPref("country_name", countries_names.get(position));
                                shop_country_id = countries.getJSONObject(position - 1).getInt("id");
                            } else {
                                new CustomTools(activity).setPrefId("country_id", position + 1);
                                new CustomTools(activity).setPref("country_name", countries_names.get(position + 1));
                            }

                            JSONArray statesJSONArray = countries_states.getJSONArray("states");
                            ArrayList<String> states_names = new ArrayList<>();
                            states_names.add("All Cities");
                            for (int i = 0; i < statesJSONArray.length(); i++) {
                                if (statesJSONArray.getJSONObject(i).getInt("country_id") == shop_country_id) {
                                    states_names.add(statesJSONArray.getJSONObject(i).getString("name"));
                                }
                            }
                            ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, states_names);
                            citySelector.setAdapter(cityArrayAdapter);
                            citySelector.setSelection(new CustomTools(activity).setPrefId("city_id" + position, null));

                            citySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        if (i != 0) {
                                            new CustomTools(activity).setPrefId("city_id" + position, i);
                                            new CustomTools(activity).setPref("city_name", states_names.get(i));
                                        } else {
                                            new CustomTools(activity).setPrefId("city_id" + position, i + 1);
                                            new CustomTools(activity).setPref("city_name", states_names.get(i + 1));
                                        }
                                    } catch (Exception e) {
                                        Log.e("errnos", e.toString());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        } catch (Exception e) {
                            Log.e("errnos", e.toString());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        } catch (Exception e) {
            Log.e("errnos", e.toString());
        }


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
        app_info.setOnClickListener(view -> {
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
}