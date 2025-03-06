/*
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft.category

import com.android.internal.logging.nano.MetricsProto
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.R
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable

@SearchIndexable
class Misc : DashboardFragment() {

    override fun getPreferenceScreenResId(): Int {
        return R.xml.craft_misc
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.FORTUNE
    }

    override fun getLogTag(): String {
        return TAG
    }

    companion object {
        private const val TAG = "Misc"

        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = BaseSearchIndexProvider(R.xml.craft_misc)
    }
}
