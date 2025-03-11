/*
 * Copyright (C) 2023 the RisingOS Android Project
 * Copyright (C) 2024-2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.view.Display
import com.android.internal.os.PowerProfile
import com.android.internal.util.MemInfoReader
import kotlin.math.ceil
import kotlin.math.roundToInt

object DeviceInfoUtils {

    fun getTotalRam(): String {
        val memInfoReader = MemInfoReader()
        memInfoReader.readMemInfo()
        val totalMemoryBytes = memInfoReader.totalSize
        val totalMemoryGB = totalMemoryBytes / (1024.0 * 1024.0 * 1024.0)
        val roundedMemoryGB = roundToNearestKnownRamSize(totalMemoryGB)
        return "$roundedMemoryGB GB"
    }

    fun getProcessor(): String {
        return Build.HARDWARE
    }

    fun getCameraInfo(context: Context): String {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraList = cameraManager.cameraIdList
        val cameraSpecs = mutableListOf<String>()

        for (cameraId in cameraList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraSpecs.add("Front Camera")
            } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                cameraSpecs.add("Rear Camera")
            }
        }

        return cameraSpecs.joinToString("\n")
    }

    private fun roundToNearestKnownRamSize(memoryGB: Double): Int {
        val knownSizes = arrayOf(1, 2, 3, 4, 6, 8, 10, 12, 16, 32, 48, 64)
        if (memoryGB <= 0) return 1
        for (size in knownSizes) {
            if (memoryGB <= size) return size
        }
        return knownSizes.last()
    }

    fun getStorageTotal(context: Context): String {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageBytes = statFs.totalBytes
        val totalStorageGB = totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val roundedStorageGB = roundToNearestKnownStorageSize(totalStorageGB)
        return if (roundedStorageGB >= 1024) {
            "${roundedStorageGB / 1024} TB"
        } else {
            "$roundedStorageGB GB"
        }
    }

    private fun roundToNearestKnownStorageSize(storageGB: Double): Int {
        val knownSizes = arrayOf(16, 32, 64, 128, 256, 512, 1024)
        if (storageGB <= 8) return ceil(storageGB).toInt()
        for (size in knownSizes) {
            if (storageGB <= size) return size
        }
        return ceil(storageGB).toInt()
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

}
