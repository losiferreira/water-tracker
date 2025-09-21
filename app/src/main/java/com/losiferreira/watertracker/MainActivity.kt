package com.losiferreira.watertracker

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.losiferreira.watertracker.presentation.navigation.WaterTrackerApp
import com.losiferreira.watertracker.service.FloatingBubbleService
import com.losiferreira.watertracker.ui.theme.WaterTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request overlay permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        
        setContent {
            WaterTrackerTheme {
                WaterTrackerApp()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            startService(Intent(this, FloatingBubbleService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, FloatingBubbleService::class.java))
    }
}