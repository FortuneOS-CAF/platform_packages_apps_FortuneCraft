/*
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft

import android.content.Context
import android.os.Bundle
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.core.lifecycle.Lifecycle
import com.android.settingslib.search.SearchIndexable

class FortuneCraft : DashboardFragment() {

    companion object {
        const val CATEGORY_KEY = "com.android.settings.category.ia.craft"
        private const val LOG_TAG = "FortuneCraft"

        val SEARCH_INDEX_DATA_PROVIDER: BaseSearchIndexProvider = object : BaseSearchIndexProvider(R.xml.craft) {
            override fun createPreferenceControllers(context: Context): List<AbstractPreferenceController> {
                return buildPreferenceControllers(context, null, null)
            }
        }

        private fun buildPreferenceControllers(context: Context, fragment: FortuneCraft?, lifecycle: Lifecycle?): List<AbstractPreferenceController> {
            val controllers = mutableListOf<AbstractPreferenceController>()
            controllers.add(FortuneCraftController(context))
            return controllers
        }
    }

    override fun getPreferenceScreenResId(): Int {
        return R.xml.craft
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.FORTUNE
    }

    override fun onStart() {
        super.onStart()
    }

    override fun getLogTag(): String {
        return LOG_TAG
    }

    override fun createPreferenceControllers(context: Context): List<AbstractPreferenceController> {
        return buildPreferenceControllers(context, this, getSettingsLifecycle())
    }
}
