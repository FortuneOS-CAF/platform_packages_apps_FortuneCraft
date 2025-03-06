/*
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.provider.SearchIndexableResource
import android.provider.Settings
import androidx.preference.Preference
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.Indexable
import com.android.settingslib.search.SearchIndexable
import org.fortune.framework.preferences.SystemSettingMasterSwitchPreference

@SearchIndexable
class FortuneCraft : DashboardFragment(), Preference.OnPreferenceChangeListener, Indexable {

    private val NETWORK_TRAFFIC_STATE = "network_traffic_state"
    private var mNetTrafficState: SystemSettingMasterSwitchPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updatePreferences()
    }

    override fun getPreferenceScreenResId(): Int = R.xml.craft

    override protected val logTag: String = "FortuneCraft"

    private fun updatePreferences() {
        val resolver = requireActivity().contentResolver

        mNetTrafficState = findPreference(NETWORK_TRAFFIC_STATE)
        mNetTrafficState?.apply {
            setOnPreferenceChangeListener(this@FortuneCraft)
            isChecked = Settings.System.getInt(resolver, NETWORK_TRAFFIC_STATE, 0) == 1
            updateNetTrafficSummary(isChecked)
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val resolver = requireActivity().contentResolver
        if (preference == mNetTrafficState) {
            val enabled = newValue as Boolean
            Settings.System.putInt(resolver, NETWORK_TRAFFIC_STATE, if (enabled) 1 else 0)
            updateNetTrafficSummary(enabled)
            return true
        }
        return false
    }

    private fun updateNetTrafficSummary(enabled: Boolean = Settings.System.getInt(
        requireActivity().contentResolver, NETWORK_TRAFFIC_STATE, 0) == 1) {

        if (mNetTrafficState == null) return

        var summary = requireActivity().getString(R.string.switch_off_text)
        if (enabled) {
            val status = Settings.System.getInt(
                requireActivity().contentResolver,
                Settings.System.NETWORK_TRAFFIC_VIEW_LOCATION, 0
            )
            val resid = when (status) {
                1 -> R.string.traffic_expanded_statusbar
                2 -> R.string.show_network_traffic_all
                else -> R.string.traffic_statusbar
            }
            summary = requireActivity().getString(R.string.network_traffic_state_summary) + " " +
                    requireActivity().getString(resid)
        }
        mNetTrafficState?.summary = summary
    }

    override fun onResume() {
        super.onResume()
        updateNetTrafficSummary()
    }

    override fun getMetricsCategory(): Int = MetricsProto.MetricsEvent.FORTUNE

    companion object {
        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = object : BaseSearchIndexProvider() {
            override fun getXmlResourcesToIndex(context: Context, enabled: Boolean): List<SearchIndexableResource> {
                return listOf(SearchIndexableResource(context).apply {
                    xmlResId = R.xml.craft
                })
            }

            override fun getNonIndexableKeys(context: Context): List<String> {
                return super.getNonIndexableKeys(context).apply {
                    add("network_traffic_state")
                }
            }
        }
    }
}
