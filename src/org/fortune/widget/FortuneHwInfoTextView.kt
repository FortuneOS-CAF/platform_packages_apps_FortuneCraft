package org.fortune.widget

import android.content.Context
import android.os.Build
import android.os.SystemProperties
import android.hardware.display.DisplayManager
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import android.app.ActivityManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.android.settings.R
import com.android.internal.os.PowerProfile
import com.android.internal.util.MemInfoReader
import kotlin.math.ceil
import kotlin.math.sqrt
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

        val deviceCodename = parentView.findViewById<TextView>(R.id.fortune_device_codename)
        val battery = parentView.findViewById<TextView>(R.id.fortune_battery_capacity)
        val ram = parentView.findViewById<TextView>(R.id.fortune_ram)
        val camera = parentView.findViewById<TextView>(R.id.fortune_camera)
        val processor = parentView.findViewById<TextView>(R.id.fortune_processor)
        val display = parentView.findViewById<TextView>(R.id.fortune_display)

        deviceCodename?.text = getDeviceCodename()
        battery?.text = getBatteryCapacity(mContext)
        ram?.text = getTotalRam()
        camera?.text = getCameraInfo(mContext)
        processor?.text = getProcessor()
        display?.text = getScreenResolution(mContext)
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

    fun getBatteryCapacity(context: Context): String {
        val powerProfile = PowerProfile(context)
        val batteryCapacity = powerProfile.getAveragePower(PowerProfile.POWER_BATTERY_CAPACITY).roundToInt().toString()
        return "${batteryCapacity} mAh"
    }

    fun getScreenResolution(context: Context): String {
        val dm = context.getSystemService(DisplayManager::class.java)
        val display = dm?.getDisplay(Display.DEFAULT_DISPLAY)
        val height = display?.mode?.physicalHeight
        val width = display?.mode?.physicalWidth
        return "${width} x ${height}"
    }

    fun getProcessor(): String {
        return if (!Build.SOC_MODEL.equals(Build.UNKNOWN)) {
            if (!Build.SOC_MANUFACTURER.equals(Build.UNKNOWN)) {
                "${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}"
            } else {
                Build.SOC_MODEL
            }
        } else {
            SystemProperties.get("ro.board.platform", "Unknown")
        }
    }

    fun getCameraInfo(context: Context): String {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIds = cameraManager.cameraIdList
        val rearCameras = mutableListOf<Int>()
        val frontCameras = mutableListOf<Int>()

        for (cameraId in cameraIds) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            val pixelArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)

            if (pixelArraySize != null) {
                val megapixels = (pixelArraySize.width * pixelArraySize.height) / 1_000_000

                when (lensFacing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> frontCameras.add(megapixels)
                    CameraCharacteristics.LENS_FACING_BACK -> rearCameras.add(megapixels)
                }
            }
        }

        rearCameras.sortDescending()

        val frontCameraText = if (frontCameras.isNotEmpty()) "Front: ${frontCameras.max()}MP" else "Front: Unknown"
        val rearCameraText = if (rearCameras.isNotEmpty()) "Rear: ${rearCameras.joinToString("MP + ")}MP" else "Rear: Unknown"

        return "$frontCameraText\n$rearCameraText"
    }

    fun getDeviceCodename(): String {
        return Build.DEVICE
    }

}
