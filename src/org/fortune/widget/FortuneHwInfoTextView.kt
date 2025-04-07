package org.fortune.widget

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.SystemProperties
import android.util.AttributeSet
import android.view.Display
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.android.internal.os.PowerProfile
import com.android.internal.util.MemInfoReader
import com.android.settings.R
import kotlin.math.roundToInt

class FortuneHwInfoTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        post { updateTextViews() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateTextViews()
    }

    private fun updateTextViews() {
        val parentView = rootView ?: return

        val deviceModel = parentView.findViewById<TextView>(R.id.fortune_device_model)
        val battery = parentView.findViewById<TextView>(R.id.fortune_battery_capacity)
        val ram = parentView.findViewById<TextView>(R.id.fortune_ram)
        val camera = parentView.findViewById<TextView>(R.id.fortune_camera)
        val processor = parentView.findViewById<TextView>(R.id.fortune_processor)
        val display = parentView.findViewById<TextView>(R.id.fortune_display)

        deviceModel?.text = getDeviceModel()
        battery?.text = getBatteryCapacity(context)
        ram?.text = getTotalRam()
        camera?.text = getCameraInfo()
        processor?.text = getProcessor()
        display?.text = getScreenResolution(context)
    }

    fun getDeviceModel(): String {
        return Build.MODEL
    }

    fun getBatteryCapacity(context: Context): String {
        val powerProfile = PowerProfile(context)
        val batteryCapacity = powerProfile.getAveragePower(PowerProfile.POWER_BATTERY_CAPACITY).roundToInt()
        return "$batteryCapacity mAh"
    }

    fun getTotalRam(): String {
        val memInfoReader = MemInfoReader()
        memInfoReader.readMemInfo()
        val totalMemoryBytes = memInfoReader.totalSize
        val totalMemoryGB = totalMemoryBytes / (1024.0 * 1024.0 * 1024.0)
        val roundedMemoryGB = roundToNearestKnownRamSize(totalMemoryGB)
        return "$roundedMemoryGB GB"
    }

    private fun roundToNearestKnownRamSize(memoryGB: Double): Int {
        val knownSizes = arrayOf(1, 2, 3, 4, 6, 8, 10, 12, 16, 32, 48, 64)
        if (memoryGB <= 0) return 1
        for (size in knownSizes) {
            if (memoryGB <= size) return size
        }
        return knownSizes.last()
    }

    fun getScreenResolution(context: Context): String {
        val dm = context.getSystemService(DisplayManager::class.java)
        val display = dm?.getDisplay(Display.DEFAULT_DISPLAY)
        val height = display?.mode?.physicalHeight
        val width = display?.mode?.physicalWidth
        return "${width} x ${height}"
    }

    fun getProcessor(): String {
        return SystemProperties.get("org.fortune.device.processor", "Unknown")
    }

    fun getCameraInfo(): String {
        val front = SystemProperties.get("org.fortune.device.camera_front", "").trim()
        val rear = SystemProperties.get("org.fortune.device.camera_rear", "").trim()

        val frontText = if (front.isNotEmpty()) "Front $front" else "Front Unknown"
        val rearText = if (rear.isNotEmpty()) "Rear $rear" else "Rear Unknown"

        return "$frontText\n$rearText"
    }
}

