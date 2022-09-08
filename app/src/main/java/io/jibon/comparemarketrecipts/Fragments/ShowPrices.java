package io.jibon.comparemarketrecipts.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.jibon.comparemarketrecipts.Adapter.GetProductsPricesAdapter;
import io.jibon.comparemarketrecipts.CustomTools;
import io.jibon.comparemarketrecipts.Internet2;
import io.jibon.comparemarketrecipts.R;

@SuppressLint("SetTextI18n")
public class ShowPrices extends Fragment {
    Activity activity;
    TextView mainActivityTitle;
    String pageTitle, selectedCountry = "", selectedCity = "", searchedItem = "";
    Boolean byYou;
    Spinner countrySelector, citySelector;
    LinearLayout nothingFoundForYourSearch;
    ProgressBar progressBar;
    ListView locationsProductsPrices_ShowPriceByCity;
    EditText searchForProductName;
    SwipeRefreshLayout swiperefresh_ShoPriceByCity;

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
        nothingFoundForYourSearch = showPricesByCityView.findViewById(R.id.nothingFoundForYourSearch);
        progressBar = showPricesByCityView.findViewById(R.id.progressBar);
        locationsProductsPrices_ShowPriceByCity = showPricesByCityView.findViewById(R.id.locationsProductsPrices_ShowPriceByCity);
        searchForProductName = showPricesByCityView.findViewById(R.id.searchForProductName);
        swiperefresh_ShoPriceByCity = showPricesByCityView.findViewById(R.id.swiperefresh_ShoPriceByCity);


        // set startup values

        if (byYou) {
            pageTitle = ("Stores & prices by you");
        } else {
            pageTitle = ("Local stores & prices");
        }

        // start activity working

        searchForProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchedItem = editable.toString().replaceAll("[^A-Za-z\\s]+", "").trim().replaceAll(" +", " ");
                updatePriceListAdapter();
            }
        });

        swiperefresh_ShoPriceByCity.setOnRefreshListener(() -> updatePriceListAdapter());

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
                                selectedCountry = countries.getJSONObject(position - 1).getString("name");
                                shop_country_id = countries.getJSONObject(position - 1).getInt("id");
                            } else {
                                selectedCountry = "";
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
                                            selectedCity = states_names.get(i);

                                        } else {
                                            selectedCity = "";
                                        }
                                        updatePriceListAdapter();
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

        // return
        return showPricesByCityView;
    }

    private void updatePriceListAdapter() {
        progressBar.setVisibility(View.VISIBLE);
        String url = new CustomTools(activity).linkForJson("comparemarketrecipts.php?getProductsPrices=" + byYou + "&city=" + selectedCity + "&country=" + selectedCountry + "&search=" + searchedItem);
        new Internet2(activity, url, (code, result) -> {
            try {
                swiperefresh_ShoPriceByCity.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                nothingFoundForYourSearch.setVisibility(View.GONE);
                if (code == 200 && result != null) {
                    if (result.has("getProductsPrices")) {
                        GetProductsPricesAdapter getProductsPricesAdapter = new GetProductsPricesAdapter(activity, result.getJSONArray("getProductsPrices"));
                        locationsProductsPrices_ShowPriceByCity.setAdapter(getProductsPricesAdapter);
                        if (result.getJSONArray("getProductsPrices").length() == 0) {
                            nothingFoundForYourSearch.setVisibility(View.VISIBLE);
                        }
                    } else {
                        nothingFoundForYourSearch.setVisibility(View.VISIBLE);
                    }
                } else {
                    new CustomTools(activity).toast("Network error!", R.drawable.ic_baseline_error_outline_24);
                }
            } catch (Exception e) {
                Log.e("errnos", e.toString());
            }
        }).execute();

    }
}