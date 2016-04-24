package brandon.payboy.brandon.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.payboy.brandon.R;

import java.util.Locale;

import brandon.payboy.brandon.util.AppPreferences;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends Activity {

    @Bind(R.id.settings_tv) TextView settingsTv;
    @Bind(R.id.your_wage_tv) TextView yourWageTv;
    @Bind(R.id.notification_tv) TextView notificationTv;
    @Bind(R.id.wage_enter_et) EditText wageEnterEt;
    @Bind(R.id.on_btn) ImageView on_btn;
    @Bind(R.id.off_btn) ImageView off_btn;

    private AppPreferences appPrefs;
    private boolean isNotificationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        appPrefs = new AppPreferences(getApplicationContext());
        applyCustomFont();
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        //Set default values (if any)
        wageEnterEt.setText(appPrefs.getWageValue() == 0 ? "" : String.format(Locale.US, "%.2f", appPrefs.getWageValue()));

        isNotificationEnabled = appPrefs.isNotificationEnabled();
        setNotificationButtonImages();
    }

    @OnClick(R.id.on_btn)
    @SuppressWarnings("unused")
    public void onButtonPressed(View view) {
        if (isNotificationEnabled) return;
        toggleNotificationSettings();
    }

    @OnClick(R.id.off_btn)
    @SuppressWarnings("unused")
    public void offButtonPressed(View view) {
        if (!isNotificationEnabled) return;
        toggleNotificationSettings();
    }

    public void toggleNotificationSettings() {
        isNotificationEnabled = !isNotificationEnabled;
        setNotificationButtonImages();
    }

    public void setNotificationButtonImages() {
        if (isNotificationEnabled) {
            on_btn.setImageResource(R.drawable.notification_on_on);
            off_btn.setImageResource(R.drawable.notification_off_off);
        } else {
            on_btn.setImageResource(R.drawable.notification_on_off);
            off_btn.setImageResource(R.drawable.notification_off_on);
        }
    }

    @OnClick(R.id.continue_btn)
    @SuppressWarnings("unused")
    public void continueButtonPressed(View view) {
        try {
            double wageValue = Double.parseDouble(wageEnterEt.getText().toString());
            saveAppSettings(wageValue, isNotificationEnabled);
            startWageActivity();
        } catch (NumberFormatException e) {
            Snackbar.make(view, "Enter a wage in the field above", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void saveAppSettings(double wageValue, boolean notificationEnabled) {
        appPrefs.setWageValue(wageValue);
        appPrefs.setNotificationEnabled(notificationEnabled);
    }

    public void startWageActivity() {
        this.startActivity(new Intent(this, WageCalculatorActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        this.finish();
    }

    public void applyCustomFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        settingsTv.setTypeface(font);
        yourWageTv.setTypeface(font);
        notificationTv.setTypeface(font);
    }
}