// SPDX-FileCopyrightText: Copyright 2026 Eden Emulator Project
// SPDX-License-Identifier: GPL-3.0-or-later

package org.yuzu.yuzu_emu.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.Rational
import android.view.Display
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.yuzu.yuzu_emu.R
import org.yuzu.yuzu_emu.features.settings.model.IntSetting
import org.yuzu.yuzu_emu.views.FixedRatioSurfaceView

class SecondaryDisplayPresentation(
    context: Context,
    display: Display,
    private val surfaceCallback: SurfaceHolder.Callback
) : Presentation(context, display) {

    lateinit var surfaceView: FixedRatioSurfaceView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_secondary_display)

        window?.let { win ->
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            win.setBackgroundDrawableResource(android.R.color.black)
            WindowCompat.setDecorFitsSystemWindows(win, false)
            WindowInsetsControllerCompat(win, win.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        surfaceView = findViewById(R.id.secondary_surface_view)
        updateAspectRatio()
        surfaceView.holder.addCallback(surfaceCallback)
    }

    fun updateAspectRatio() {
        if (!::surfaceView.isInitialized) return
        val aspectRatio = when (IntSetting.RENDERER_ASPECT_RATIO.getInt()) {
            0 -> Rational(16, 9)
            1 -> Rational(4, 3)
            2 -> Rational(21, 9)
            3 -> Rational(16, 10)
            else -> null // Stretch / Best fit
        }
        surfaceView.setAspectRatio(aspectRatio)
    }
}
