/*
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft

import com.android.internal.logging.nano.MetricsProto
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.R
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable

@SearchIndexable
class FortuneCraft : DashboardFragment() {

    override fun getPreferenceScreenResId(): Int {
        return R.xml.craft
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.FORTUNE
    }

    override fun getLogTag(): String {
        return TAG
    }

    companion object {
        const val CATEGORY_KEY = "com.android.settings.category.ia.craft"
        private const val TAG = "FortuneCraft"

        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = BaseSearchIndexProvider(R.xml.craft)
    }
}
