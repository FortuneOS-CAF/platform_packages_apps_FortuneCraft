/*
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragmentCompat;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import org.fortune.framework.preferences.SystemSettingMasterSwitchPreference;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class FortuneCraft extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";

    private SystemSettingMasterSwitchPreference mNetTrafficState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();
        addPreferencesFromResource(R.xml.craft);

        final Resources resources = getResources();

        mNetTrafficState = findPreference(NETWORK_TRAFFIC_STATE);
        mNetTrafficState.setOnPreferenceChangeListener(this);
        boolean enabled = Settings.System.getInt(resolver,
                NETWORK_TRAFFIC_STATE, 0) == 1;
        mNetTrafficState.setChecked(enabled);
        updateNetTrafficSummary(enabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficState) {
            boolean enabled = (boolean) newValue;
            Settings.System.putInt(resolver, NETWORK_TRAFFIC_STATE, enabled ? 1 : 0);
            updateNetTrafficSummary(enabled);
            return true;
        }
        return false;
    }

    private void updateNetTrafficSummary() {
        final boolean enabled = Settings.System.getInt(
                getActivity().getContentResolver(),
                NETWORK_TRAFFIC_STATE, 0) == 1;
        updateNetTrafficSummary(enabled);
    }

    private void updateNetTrafficSummary(boolean enabled) {
        if (mNetTrafficState == null) return;
        String summary = getActivity().getString(R.string.switch_off_text);
        if (enabled) {
            final int status = Settings.System.getInt(
                    getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_VIEW_LOCATION, 0);
            int resid = R.string.traffic_statusbar;
            if (status == 1) resid = R.string.traffic_expanded_statusbar;
            else if (status == 2) resid = R.string.show_network_traffic_all;
            summary = getActivity().getString(R.string.network_traffic_state_summary)
                    + " " + getActivity().getString(resid);
        }
        mNetTrafficState.setSummary(summary);
    }

    private void updateNetTrafficValue() {
        if (mNetTrafficState == null) return;
        boolean enabled = Settings.System.getInt(
                getActivity().getContentResolver(),
                NETWORK_TRAFFIC_STATE, 0) == 1;
        mNetTrafficState.setChecked(enabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNetTrafficSummary();
        updateNetTrafficValue();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FORTUNE;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.craft;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    keys.add(NETWORK_TRAFFIC_STATE);
                    return keys;
                }
            };
}
