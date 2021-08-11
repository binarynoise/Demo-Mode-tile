package com.franco.demomode.tiles

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.franco.demomode.R
import com.franco.demomode.Utils
import kotlinx.coroutines.runBlocking

class DemoModeTile : TileService() {
    override fun onStartListening() {
        val isDemoMode = runBlocking { Utils.isDemoModeOn(applicationContext) }
        val hasPermissions = runBlocking {
            Utils.isDumpPermissionGranted(applicationContext)
                && Utils.isWriteSecureSettingsPermissionGranted(applicationContext)
        }
        
        qsTile?.apply {
            state = when {
                !hasPermissions -> Tile.STATE_UNAVAILABLE
                isDemoMode -> Tile.STATE_ACTIVE
                else -> Tile.STATE_INACTIVE
            }
            
            icon = when {
                isDemoMode -> Icon.createWithResource(applicationContext, R.drawable.ic_on)
                else -> Icon.createWithResource(applicationContext, R.drawable.ic_off)
            }
            
            updateTile()
        }
    }
    
    override fun onClick() {
        qsTile?.apply {
            if (state == Tile.STATE_ACTIVE) {
                state = Tile.STATE_INACTIVE
                icon = Icon.createWithResource(applicationContext, R.drawable.ic_off)
                runBlocking { Utils.disableDemoMode(applicationContext) }
            } else {
                state = Tile.STATE_ACTIVE
                icon = Icon.createWithResource(applicationContext, R.drawable.ic_on)
                runBlocking { Utils.enableDemoMode(applicationContext) }
            }
            updateTile()
        }
    }
}
