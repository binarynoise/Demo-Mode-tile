package com.franco.demomode

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Utils {
    private val UI_DEMO_ACTION = "com.android.systemui.demo"
    private const val DEMO_MODE_ALLOWED = "sysui_demo_allowed"
    private const val DEMO_MODE_ON = "sysui_tuner_demo_on"
    
    suspend fun enableDemoMode(context: Context) = withContext(Dispatchers.IO) {
        if (!isDemoModeAllowed(context)) {
            Settings.Global.putInt(context.contentResolver, DEMO_MODE_ALLOWED, 1)
        }
        
        Settings.Global.putInt(context.contentResolver, DEMO_MODE_ON, 1)
        
        // clock with the latest release string
        val clock = Intent(UI_DEMO_ACTION)
        clock.putExtra("command", "clock")
        clock.putExtra("hhmm", "1100")
        context.sendBroadcast(clock)
        
        // battery icon needs to be perfect
        val battery = Intent(UI_DEMO_ACTION)
        battery.putExtra("command", "battery")
        battery.putExtra("level", "100")
        battery.putExtra("plugged", "false")
        context.sendBroadcast(battery)
        
        // signal icon
        val data = Intent(UI_DEMO_ACTION)
        data.putExtra("command", "network")
        data.putExtra("mobile", "show")
        data.putExtra("datatype", "hide")
        data.putExtra("level", "4")
        // probable fix for https://github.com/franciscofranco/Demo-Mode-tile/issues/7
        data.putExtra("sims", 1)
        context.sendBroadcast(data)
        
        // mock sim carrier connection
        val signal = Intent(UI_DEMO_ACTION)
        signal.putExtra("command", "network")
        signal.putExtra("fully", "true")
        context.sendBroadcast(signal)
        
        // WiFi icon
        val wifi = Intent(UI_DEMO_ACTION)
        wifi.putExtra("command", "network")
        wifi.putExtra("wifi", "show")
        wifi.putExtra("level", "4")
        context.sendBroadcast(wifi)
        
        // rip icons
        val miscNetwork = Intent(UI_DEMO_ACTION)
        miscNetwork.putExtra("command", "network")
        miscNetwork.putExtra("airplane", "hide")
        miscNetwork.putExtra("nosim", "hide")
        miscNetwork.putExtra("sims", 1)
        context.sendBroadcast(miscNetwork)
        
        // if there's one thing I hate is cluttered statusbar with notifs
        val notifs = Intent(UI_DEMO_ACTION)
        notifs.putExtra("command", "notifications")
        notifs.putExtra("visible", "false")
        context.sendBroadcast(notifs)
        
        // goodbye more icons!
        val miscIcons = Intent(UI_DEMO_ACTION)
        miscIcons.putExtra("command", "status")
        miscIcons.putExtra("bluetooth", "hide")
        miscIcons.putExtra("volume", "hide")
        miscIcons.putExtra("mute", "hide")
        context.sendBroadcast(miscIcons)
    }
    
    suspend fun disableDemoMode(context: Context) {
        withContext(Dispatchers.IO) {
            Settings.Global.putInt(context.contentResolver, DEMO_MODE_ON, 0)
        }
        val disableDemoMode = Intent(UI_DEMO_ACTION)
        disableDemoMode.putExtra("command", "exit")
        context.sendBroadcast(disableDemoMode)
    }
    
    suspend fun isDemoModeAllowed(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            Settings.Global.getInt(context.contentResolver, DEMO_MODE_ALLOWED) == 1
        } catch (e: SettingNotFoundException) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            false
        }
    }
    
    suspend fun isDemoModeOn(context: Context): Boolean = withContext(Dispatchers.IO) {
        Settings.Global.getInt(context.contentResolver, DEMO_MODE_ON, 0) != 0
    }
    
    suspend fun isDumpPermissionGranted(context: Context): Boolean = withContext(Dispatchers.IO) {
        (context.packageManager.checkPermission(Manifest.permission.DUMP, context.packageName)
            == PackageManager.PERMISSION_GRANTED)
    }
    
    suspend fun isWriteSecureSettingsPermissionGranted(context: Context): Boolean = withContext(Dispatchers.IO) {
        (context.packageManager.checkPermission(
            Manifest.permission.WRITE_SECURE_SETTINGS, context.packageName)
            == PackageManager.PERMISSION_GRANTED)
    }
}
