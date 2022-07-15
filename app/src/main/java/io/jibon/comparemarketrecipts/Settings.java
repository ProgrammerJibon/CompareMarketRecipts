package io.jibon.comparemarketrecipts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Settings {
    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEditor;
    protected Activity activity;
    protected String JSONLINK;

    public Settings(Activity activity) {
        this.activity = activity;
        this.JSONLINK = "http://10.5.3.231/";
    }

    public String linkForJson(String extra) {
        return this.JSONLINK + extra;
    }

    public String userId() {
        return new Settings(activity).userId(null);
    }

    public Boolean visualModeSettings() {
        try {
            preferencesEditor = preferences.edit();
            if (preferences.getString("nightMode", "exception").contains("true")) {
                preferencesEditor.putString("nightMode", "false");
            } else if (preferences.getString("nightMode", "exception").contains("false")) {
                preferencesEditor.putString("nightMode", "true");
            } else {
                preferencesEditor.putString("nightMode", "true");
            }
            preferencesEditor.apply();
            return setVisualMode();
        } catch (Exception e) {
            Log.e("errnos_settings_a", e.toString());
            return false;
        }

    }

    public Boolean setVisualMode() {
        try {
            if (preferences.getString("nightMode", "").contains("true")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return true;
            } else if (preferences.getString("nightMode", "").contains("false")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            } else {
                return visualModeSettings();
            }
        } catch (Exception e) {
            Log.e("errnos_settings_b", e.toString());
            return false;
        }

    }


    public String storedCookie(String cookies) {
        String cookie = "";
        if (cookies != null) {
            preferencesEditor = preferences.edit();
            preferencesEditor.putString("cookie", cookies);
            preferencesEditor.commit();
        }
        if (!preferences.getString("cookie", "null").equals("null")) {
            cookie = preferences.getString("cookie", "null");
        } else {
            preferencesEditor = preferences.edit();
            preferencesEditor.putString("cookie", "null");
            preferencesEditor.commit();
        }
        return cookie;
    }

    public JSONObject countries_states() {
        InputStream countries_states = activity.getResources().openRawResource(R.raw.countries_states);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JSONObject jsonObject;
        int i;
        try {
            i = countries_states.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = countries_states.read();
            }
            countries_states.close();
            jsonObject = new JSONObject(String.valueOf(byteArrayOutputStream));
        } catch (Exception e) {
            jsonObject = null;
            e.printStackTrace();
        }

        return jsonObject;
    }


    public String userId(String user) {
        String user_id = "";
        if (user != null) {
            preferencesEditor = preferences.edit();
            preferencesEditor.putString("user_id", user);
            preferencesEditor.commit();
        }
        if (!preferences.getString("user_id", "null").equals("null")) {
            user_id = preferences.getString("user_id", "null");
        } else {
            preferencesEditor = preferences.edit();
            preferencesEditor.putString("user_id", "null");
            preferencesEditor.commit();
        }
        return user_id;
    }

    public boolean toast(String text, Integer drawable) {
        try {
            View view = activity.getLayoutInflater().inflate(R.layout.toast, activity.findViewById(R.id.Custom_toast), false);
            ((TextView) view.findViewById(R.id.Custom_toast_text)).setText(text);
            if (drawable != null) {
                ((ImageView) view.findViewById(R.id.Custom_toast_icon)).setImageResource(drawable);
            }
            Toast toast = new Toast(activity.getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                new Settings(activity).vibrate(50);
            }
            return true;
        } catch (Exception e) {
            Log.e("errnos_ctool_a", "Custom Toast Problem: " + e.toString());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean vibrate(int milliseconds) {
        try {
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(milliseconds);
            vibrator.vibrate(Vibrator.VIBRATION_EFFECT_SUPPORT_YES);
            return true;
        } catch (Exception e) {
            Log.e("errnos_ctool_b", "Vibrate Problem: " + e.toString());
            return false;
        }
    }

    public void alert(String title, String messages, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(messages)
                .setIcon(icon)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                })
                .setCancelable(true);
        builder.create().show();
    }
}
