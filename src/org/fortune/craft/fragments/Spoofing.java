package org.fortune.craft.fragments;

import android.os.Bundle;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Spoofing extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return 0;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.spoofing_settings, rootKey);

        getActivity().setTitle(R.string.fortune_spoofing_dashboard_title);

        SwitchPreference spoofGphotosPreference = (SwitchPreference) findPreference("spoof_gphotos");
        if (spoofGphotosPreference != null) {
            String gphotosProp = SystemProperties.get("persist.sys.fortuneos.gphotos", "false");
            spoofGphotosPreference.setChecked(!gphotosProp.equals("false"));

            spoofGphotosPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    SystemProperties.set("persist.sys.fortuneos.gphotos", "true");
                } else {
                    SystemProperties.set("persist.sys.fortuneos.gphotos", "false");
                }
                return true;
            });
        }

        SwitchPreference spoofGoogleAppsPreference = (SwitchPreference) findPreference("spoof_google_apps");
        if (spoofGoogleAppsPreference != null) {
            String gappsProp = SystemProperties.get("persist.sys.fortuneos.gapps", "false");
            spoofGoogleAppsPreference.setChecked(!gappsProp.equals("false"));

            spoofGoogleAppsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    SystemProperties.set("persist.sys.fortuneos.gapps", "true");
                } else {
                    SystemProperties.set("persist.sys.fortuneos.gapps", "false");
                }
                return true;
            });
        }

        SwitchPreference spoofGamesPreference = (SwitchPreference) findPreference("spoof_games");
        if (spoofGamesPreference != null) {
            String gamesProp = SystemProperties.get("persist.sys.fortuneos.spoofgames", "false");
            spoofGamesPreference.setChecked(gamesProp.equals("true"));

            spoofGamesPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    SystemProperties.set("persist.sys.fortuneos.spoofgames", "true");
                } else {
                    SystemProperties.set("persist.sys.fortuneos.spoofgames", "false");
                }
                return true;
            });
        }
    }
}
