/*
 * Copyright (C) FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class FortuneCraft extends DashboardFragment {

    private static final String TAG = "FortuneCraft";

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.craft;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.FORTUNE;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.craft);
}
