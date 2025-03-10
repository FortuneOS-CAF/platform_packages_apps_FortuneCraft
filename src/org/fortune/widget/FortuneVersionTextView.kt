/*
 * Copyright (C) 2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.widget

import android.content.Context
import android.os.Build
import android.os.SystemProperties
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class FortuneVersionTextView : AppCompatTextView {

    companion object {
        private const val FORTUNE_BUILD_VERSION = "org.fortune.build.version"
        private const val FORTUNE_CODENAME = "org.fortune.codename"
        private const val FORTUNE_RELEASE_TYPE = "org.fortune.releasetype"
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        var version = SystemProperties.get(FORTUNE_BUILD_VERSION, "Unknown")
        var codename = SystemProperties.get(FORTUNE_CODENAME, "Unknown")
        var releaseType = SystemProperties.get(FORTUNE_RELEASE_TYPE, "Unknown")

        text = "$version | $codename | $releaseType"
    }
}
