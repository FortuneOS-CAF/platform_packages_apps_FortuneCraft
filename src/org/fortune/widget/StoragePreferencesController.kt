package org.fortune.widget

import android.content.Context
import android.os.storage.StorageManager
import android.text.BidiFormatter
import android.text.format.Formatter
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.core.BasePreferenceController
import com.android.settingslib.deviceinfo.PrivateStorageInfo
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider
import com.android.settingslib.utils.ThreadUtils
import java.util.concurrent.Future

class StoragePreferencesController(context: Context, key: String) : BasePreferenceController(context, key) {

    private val mStorageManager: StorageManager? =
        context.getSystemService(StorageManager::class.java)

    private val mStorageManagerVolumeProvider = StorageManagerVolumeProvider(mStorageManager)

    private var mPreference: StoragePreference? = null

    override fun getAvailabilityStatus(): Int {
        return AVAILABLE
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        mPreference = screen.findPreference(getPreferenceKey()) as? StoragePreference
        updateState(mPreference)
    }

    override fun updateState(preference: Preference?) {
        if (preference is StoragePreference) {
            val info = PrivateStorageInfo.getPrivateStorageInfo(getStorageManagerVolumeProvider())
            val totalBytes = info.totalBytes
            val usedBytes = totalBytes - info.freeBytes

            val instance = BidiFormatter.getInstance()
            val totalSummary = instance.unicodeWrap(Formatter.formatShortFileSize(mContext, totalBytes))
            val usedSummary = Formatter.formatShortFileSize(mContext, usedBytes)

            preference.setProgress(((usedBytes.toDouble() / totalBytes.toDouble()) * 100.0).toInt())
            preference.summary = "$usedSummary / $totalSummary"
        }
    }

    override fun refreshSummary(preference: Preference) {
        refreshSummaryThread(preference)
    }

    private fun refreshSummaryThread(preference: Preference): Future<*> {
        return ThreadUtils.postOnBackgroundThread {
            ThreadUtils.postOnMainThread {
                updateState(preference)
            }
        }
    }

    private fun getStorageManagerVolumeProvider(): StorageManagerVolumeProvider {
        return mStorageManagerVolumeProvider
    }
}
