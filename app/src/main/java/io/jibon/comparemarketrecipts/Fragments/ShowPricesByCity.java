package io.jibon.comparemarketrecipts.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.jibon.comparemarketrecipts.R;

@SuppressLint("SetTextI18n")
public class ShowPricesByCity extends Fragment {
    Activity activity;
    TextView mainActivityTitle;
    String pageTitle;

    public ShowPricesByCity() {

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
        View ShowPricesByCityView = inflater.inflate(R.layout.fragment_show_prices_by_city, container, false);
        // find blocks
        mainActivityTitle = activity.findViewById(R.id.mainActivityTitle);

        // set startup values
        pageTitle = ("Loading local stores & prices");
        return ShowPricesByCityView;
    }
}