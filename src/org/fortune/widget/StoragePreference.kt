package org.fortune.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class StoragePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    private var progressBarValue: Int = -1

    fun setProgress(progress: Int) {
        progressBarValue = progress
        notifyChanged()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val progressView = holder.findViewById(android.R.id.progress)
        if (progressView is ProgressBar) {
            if (progressBarValue < 0) {
                progressView.isIndeterminate = true
            } else {
                progressView.isIndeterminate = false
                progressView.progress = progressBarValue
            }
        }
    }
}
