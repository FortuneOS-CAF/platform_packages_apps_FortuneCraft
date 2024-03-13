/*
 *  Copyright (C) 2024 FortuneOS
 *  SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.craft.fragments;

import android.os.Bundle;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class ChangelogActivity extends CollapsingToolbarBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ChangelogFragment())
                .commit();
    }
}
