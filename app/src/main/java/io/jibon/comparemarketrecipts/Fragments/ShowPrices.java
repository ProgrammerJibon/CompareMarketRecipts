package io.jibon.comparemarketrecipts.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.jibon.comparemarketrecipts.R;
import io.jibon.comparemarketrecipts.Settings;

@SuppressLint("SetTextI18n")
public class ShowPrices extends Fragment {
    Activity activity;
    TextView mainActivityTitle;
    String pageTitle;
    Boolean byYou;
    Spinner countrySelector, citySelector;

    public ShowPrices(boolean byYou) {
        this.byYou = byYou;
    }

    @Override
    public void onAttach(@NonNull Activity context) {
        super.onAttach(context);
        this.activity = context;
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
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View showPricesByCityView = inflater.inflate(R.layout.fragment_show_prices_by_city, container, false);
        // find blocks
        mainActivityTitle = activity.findViewById(R.id.mainActivityTitle);
        countrySelector = showPricesByCityView.findViewById(R.id.countrySelector);
        citySelector = showPricesByCityView.findViewById(R.id.citySelector);

        // set startup values

        if (byYou) {
            pageTitle = ("Stores & prices by you");
        } else {
            pageTitle = ("Local stores & prices");
        }

        // start activity working
        try {
            JSONObject countries_states = new Settings(activity).countries_states();
            if (countries_states.has("countries")) {
                JSONArray countries = countries_states.getJSONArray("countries");
                ArrayList<String> countries_names = new ArrayList<>();
                countries_names.add("All Countries");
                for (int i = 0; i < countries.length(); i++) {
                    countries_names.add(countries.getJSONObject(i).getString("name"));
                }
                ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, countries_names);
                countrySelector.setAdapter(countryArrayAdapter);

                countrySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        try {
                            int shop_country_id = -1;
                            if (position != 0) {
                                shop_country_id = countries.getJSONObject(position).getInt("id") + 1;
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

        // return
        return showPricesByCityView;
    }
}