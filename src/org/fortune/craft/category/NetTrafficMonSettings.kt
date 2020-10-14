/*
 * Copyright (C) 2020 Yet Another AOSP Project
 * Copyright (C) 2025 FortuneOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fortune.craft.category

import android.content.ContentResolver
import android.os.Bundle
import android.os.UserHandle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import androidx.preference.*
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable
import com.android.settingslib.widget.MainSwitchPreference
import org.fortune.framework.preferences.CustomSeekBarPreference
import org.fortune.framework.preferences.SystemSettingSwitchPreference

@SearchIndexable
class NetTrafficMonSettings : DashboardFragment(), Preference.OnPreferenceChangeListener {

    companion object {
        private const val TAG = "NetTrafficMonSettings"
        private const val KEY_MASTER = "network_traffic_state"
        private const val NETWORK_TRAFFIC_FONT_SIZE = "network_traffic_font_size"
        private const val NETWORK_TRAFFIC_LOCATION = "network_traffic_location"
        private const val NETWORK_TRAFFIC_LIMIT_MB = "network_traffic_limit_mb"
        private const val NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold"
        private const val NETWORK_TRAFFIC_ARROW = "network_traffic_arrow"

        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = BaseSearchIndexProvider(R.xml.craft_net_traffic)
    }

    private var mMasterSwitch: MainSwitchPreference? = null
    private var mNetTrafficLocation: ListPreference? = null
    private var mThresholdMb: SwitchPreferenceCompat? = null
    private var mThreshold: CustomSeekBarPreference? = null
    private var mShowArrows: SystemSettingSwitchPreference? = null
    private var mNetTrafficType: ListPreference? = null
    private var mNetTrafficSize: CustomSeekBarPreference? = null

    override fun getPreferenceScreenResId(): Int {
        return R.xml.craft_net_traffic
    }

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        val resolver: ContentResolver = contentResolver

        mMasterSwitch = findPreference(KEY_MASTER)
        val enabled = Settings.System.getInt(resolver, KEY_MASTER, 0) == 1
        mMasterSwitch?.isChecked = enabled
        mMasterSwitch?.addOnSwitchChangeListener { _: CompoundButton, isChecked: Boolean ->
            Settings.System.putInt(contentResolver, KEY_MASTER, if (isChecked) 1 else 0)
            updateMasterEnablement(isChecked)
        }
        updateMasterEnablement()

        val type = Settings.System.getIntForUser(resolver, Settings.System.NETWORK_TRAFFIC_TYPE, 0, UserHandle.USER_CURRENT)
        mNetTrafficType = findPreference("network_traffic_type")
        mNetTrafficType?.apply {
            value = type.toString()
            summary = entry
            onPreferenceChangeListener = this@NetTrafficMonSettings
        }

        val netTrafficSize = Settings.System.getInt(resolver, Settings.System.NETWORK_TRAFFIC_FONT_SIZE, 36)
        mNetTrafficSize = findPreference(NETWORK_TRAFFIC_FONT_SIZE)
        mNetTrafficSize?.apply {
            value = netTrafficSize
            onPreferenceChangeListener = this@NetTrafficMonSettings
        }

        mNetTrafficLocation = findPreference(NETWORK_TRAFFIC_LOCATION)
        val location = Settings.System.getIntForUser(resolver, Settings.System.NETWORK_TRAFFIC_VIEW_LOCATION, 0, UserHandle.USER_CURRENT)
        mNetTrafficLocation?.apply {
            onPreferenceChangeListener = this@NetTrafficMonSettings
            setValue(location.toString())
            summary = entry
        }

        mThreshold = findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD)
        var value = Settings.System.getIntForUser(resolver, Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT)
        val isMB = value > (mThreshold?.max ?: 0)
        if (isMB) {
            value /= 1000
            mThreshold?.setUnits(getString(R.string.unit_mbps))
        }
        mThreshold?.apply {
            this.value = value
            onPreferenceChangeListener = this@NetTrafficMonSettings
        }

        mThresholdMb = findPreference(NETWORK_TRAFFIC_LIMIT_MB)
        mThresholdMb?.apply {
            isChecked = isMB
            onPreferenceChangeListener = this@NetTrafficMonSettings
        }

        mShowArrows = findPreference(NETWORK_TRAFFIC_ARROW)
    }

    override fun onPreferenceChange(preference: Preference, objValue: Any): Boolean {
        return when (preference) {
            mNetTrafficLocation -> {
                val location = (objValue as String).toInt()
                Settings.System.putIntForUser(contentResolver, Settings.System.NETWORK_TRAFFIC_VIEW_LOCATION, location, UserHandle.USER_CURRENT)
                val index = mNetTrafficLocation?.findIndexOfValue(objValue)
                mNetTrafficLocation?.summary = mNetTrafficLocation?.entries?.get(index ?: 0)
                true
            }
            mThreshold -> {
                var valInt = objValue as Int
                if (mThresholdMb?.isChecked == true) valInt *= 1000
                Settings.System.putIntForUser(contentResolver, Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, valInt, UserHandle.USER_CURRENT)
                true
            }
            mThresholdMb -> {
                setLimitInMb(objValue as Boolean)
                true
            }
            mNetTrafficType -> {
                val valInt = (objValue as String).toInt()
                Settings.System.putIntForUser(contentResolver, Settings.System.NETWORK_TRAFFIC_TYPE, valInt, UserHandle.USER_CURRENT)
                val index = mNetTrafficType?.findIndexOfValue(objValue)
                mNetTrafficType?.summary = mNetTrafficType?.entries?.get(index ?: 0)
                true
            }
            mNetTrafficSize -> {
                val width = objValue as Int
                Settings.System.putInt(contentResolver, Settings.System.NETWORK_TRAFFIC_FONT_SIZE, width)
                true
            }
            else -> false
        }
    }

    private fun updateMasterEnablement() {
        val enabled = Settings.System.getInt(contentResolver, KEY_MASTER, 0) == 1
        updateMasterEnablement(enabled)
    }

    private fun updateMasterEnablement(enabled: Boolean) {
        val screen: PreferenceScreen = preferenceScreen
        for (i in 0 until screen.preferenceCount) {
            val pref = screen.getPreference(i)
            if (KEY_MASTER == pref.key) continue
            pref.isEnabled = enabled
        }
    }

    private fun setLimitInMb(value: Boolean) {
        mThreshold?.setUnits(getString(if (value) R.string.unit_mbps else R.string.unit_kbps))
        var limit = mThreshold?.value ?: 0
        if (value) limit *= 1000
        Settings.System.putIntForUser(contentResolver, Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, limit, UserHandle.USER_CURRENT)
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.FORTUNE
    }

    override fun getLogTag(): String {
        return TAG
    }
}
