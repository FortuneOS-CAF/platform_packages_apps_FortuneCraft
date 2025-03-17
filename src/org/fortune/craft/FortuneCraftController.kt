package org.fortune.craft

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference

class FortuneCraftController(context: Context) : AbstractPreferenceController(context) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        screen.findPreference<LayoutPreference>(KEY_CRAFT_HOMEPAGE)?.let { craftPref ->
            setupCraftClickListeners(craftPref)
        }
    }

    private fun setupCraftClickListeners(preference: LayoutPreference) {
        val craftClickMap = mapOf(
            R.id.craft_statusbar to "com.android.settings.Settings\$CraftStatusbarActivity",
            R.id.craft_quicksettings to "com.android.settings.Settings\$CraftQuicksettingsActivity",
            R.id.craft_navigation to "com.android.settings.Settings\$CraftNavigationActivity",
            R.id.craft_lockscreen to "com.android.settings.Settings\$CraftLockscreenActivity",
            R.id.craft_about to "com.android.settings.Settings\$CraftAboutActivity",
            R.id.craft_misc to "com.android.settings.Settings\$CraftMiscActivity"
        )
        craftClickMap.forEach { (viewId, activityName) ->
            preference.findViewById<View>(viewId)?.setOnClickListener {
                mContext.startActivity(createIntent(activityName))
            }
        }
    }

    private fun createIntent(activityName: String): Intent {
        return Intent().setComponent(ComponentName("com.android.settings", activityName))
    }

    override fun isAvailable(): Boolean = true

    override fun getPreferenceKey(): String = KEY_CRAFT_HOMEPAGE

    companion object {
        private const val KEY_CRAFT_HOMEPAGE = "craft_homepage"
    }
}
