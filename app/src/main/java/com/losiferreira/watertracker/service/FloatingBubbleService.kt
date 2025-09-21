package com.losiferreira.watertracker.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.animation.AccelerateDecelerateInterpolator
import com.losiferreira.watertracker.R
import com.losiferreira.watertracker.MainActivity
import kotlin.math.abs
import kotlin.math.sqrt

class FloatingBubbleService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var removeZoneView: View? = null
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var isDragging: Boolean = false
    private val removeZoneThreshold = 150f // Distance in pixels to activate remove zone

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createFloatingBubble()
    }

    private fun createFloatingBubble() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // Get screen dimensions
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_bubble_layout, null)
        
        val params = WindowManager.LayoutParams(
            120,
            120,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        windowManager?.addView(floatingView, params)
        
        // Create remove zone (initially hidden)
        createRemoveZone()

        floatingView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0f
            private var initialTouchY: Float = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = (event.rawX - initialTouchX).toInt()
                        val diffY = (event.rawY - initialTouchY).toInt()
                        
                        if (isDragging) {
                            // Check if released over remove zone
                            val removeZoneCenterX = screenWidth / 2
                            val removeZoneCenterY = (screenHeight * 3) / 4
                            val bubbleCenterX = params.x + 60 // half of bubble size
                            val bubbleCenterY = params.y + 60
                            
                            val distance = sqrt(
                                ((bubbleCenterX - removeZoneCenterX) * (bubbleCenterX - removeZoneCenterX) +
                                (bubbleCenterY - removeZoneCenterY) * (bubbleCenterY - removeZoneCenterY)).toFloat()
                            )
                            
                            if (distance < removeZoneThreshold) {
                                // Remove bubble
                                stopSelf()
                                return true
                            }
                            
                            // Hide remove zone
                            hideRemoveZone()
                            isDragging = false
                        } else if (abs(diffX) < 10 && abs(diffY) < 10) {
                            // Click - open app
                            val intent = Intent(this@FloatingBubbleService, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            stopSelf()
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val diffX = (event.rawX - initialTouchX).toInt()
                        val diffY = (event.rawY - initialTouchY).toInt()
                        
                        // Start dragging if moved enough
                        if (!isDragging && (abs(diffX) > 20 || abs(diffY) > 20)) {
                            isDragging = true
                            showRemoveZone()
                        }
                        
                        if (isDragging) {
                            // Calculate new position
                            var newX = initialX + diffX
                            var newY = initialY + diffY
                            
                            // Constrain to screen bounds
                            val bubbleSize = 120
                            newX = kotlin.math.max(0, kotlin.math.min(newX, screenWidth - bubbleSize))
                            newY = kotlin.math.max(0, kotlin.math.min(newY, screenHeight - bubbleSize - 200))
                            
                            params.x = newX
                            params.y = newY
                            windowManager?.updateViewLayout(floatingView, params)
                            
                            // Update remove zone appearance based on distance
                            updateRemoveZoneAppearance(params.x + 60, params.y + 60)
                        }
                        return true
                    }
                }
                return false
            }
        })
    }
    
    private fun createRemoveZone() {
        removeZoneView = LayoutInflater.from(this).inflate(R.layout.remove_zone_layout, null)
        
        val removeZoneParams = WindowManager.LayoutParams(
            120,
            120,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        
        removeZoneParams.gravity = Gravity.TOP or Gravity.LEFT
        removeZoneParams.x = (screenWidth - 120) / 2 // Center horizontally
        removeZoneParams.y = (screenHeight * 3) / 4 - 60 // 3/4 down the screen
        
        removeZoneView?.visibility = View.GONE
        windowManager?.addView(removeZoneView, removeZoneParams)
    }
    
    private fun showRemoveZone() {
        removeZoneView?.let { view ->
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
    
    private fun hideRemoveZone() {
        removeZoneView?.let { view ->
            view.animate()
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    view.visibility = View.GONE
                }
                .start()
        }
    }
    
    private fun updateRemoveZoneAppearance(bubbleCenterX: Int, bubbleCenterY: Int) {
        removeZoneView?.let { view ->
            val removeZoneCenterX = screenWidth / 2
            val removeZoneCenterY = (screenHeight * 3) / 4
            
            val distance = sqrt(
                ((bubbleCenterX - removeZoneCenterX) * (bubbleCenterX - removeZoneCenterX) +
                (bubbleCenterY - removeZoneCenterY) * (bubbleCenterY - removeZoneCenterY)).toFloat()
            )
            
            // Scale and change alpha based on proximity
            val proximity = (removeZoneThreshold - distance) / removeZoneThreshold
            val scale = if (proximity > 0) {
                1f + (proximity * 0.3f) // Grow up to 30% larger when close
            } else {
                1f
            }
            
            val alpha = if (distance < removeZoneThreshold) 0.9f else 0.6f
            
            view.scaleX = scale
            view.scaleY = scale
            view.alpha = alpha
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let {
            windowManager?.removeView(it)
        }
        removeZoneView?.let {
            windowManager?.removeView(it)
        }
    }
}