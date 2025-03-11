/*
 * Copyright (C) 2024 Declan Project
 * Copyright (C) 2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class FortuneStorageView extends Preference {
    
    private int mProgress = -1;
    
    public FortuneStorageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public final void setProgress(int progress) {
        mProgress = progress;
        notifyChanged();
    }
    
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View progressView = holder.findViewById(android.R.id.progress);
        if (progressView instanceof CircularProgressIndicator) {
            CircularProgressIndicator progressIndicator = (CircularProgressIndicator) progressView;
            if (mProgress < 0) {
                progressIndicator.setIndeterminate(true);
            } else {
                progressIndicator.setIndeterminate(false);
                progressIndicator.setProgress(mProgress);
            }
        }
    }
}