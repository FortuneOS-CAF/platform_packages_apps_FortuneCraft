/*
 * Copyright (C) 2023 the RisingOS Android Project
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft

import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference
import org.fortune.utils.DeviceInfoUtils

class FortuneController(context: Context) : AbstractPreferenceController(context) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val fortuneHWinfo = screen.findPreference<LayoutPreference>(KEY_FORTUNE_HW_INFO)

        fortuneHWinfo?.apply {
            findViewById<TextView>(R.id.fortune_battery_capacity)?.text = DeviceInfoUtils.getBatteryCapacity(mContext)
            findViewById<TextView>(R.id.fortune_ram)?.text = DeviceInfoUtils.getTotalRam()
            findViewById<TextView>(R.id.fortune_camera)?.text = DeviceInfoUtils.getCameraInfo(context)
            findViewById<TextView>(R.id.fortune_processor)?.text = DeviceInfoUtils.getProcessor()
            findViewById<TextView>(R.id.fortune_display)?.text = DeviceInfoUtils.getScreenResolution(mContext)
        }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_DEVICE_INFO
    }

    companion object {
        private const val KEY_FORTUNE_HW_INFO = "fortune_about_hwinfo"
        private const val KEY_DEVICE_INFO = "my_device_info_header"
    }
}
