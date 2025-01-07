package com.example.music.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.example.androidprojecttest1.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Balaca ikon görünməməlidir
        binding.smallLogoImage.visibility = android.view.View.INVISIBLE

        // Parıltı effekti üçün animasiya
        val glowAnimation = ObjectAnimator.ofFloat(binding.logoImage, "alpha", 0f, 1f, 0f)
        glowAnimation.duration = 2000
        glowAnimation.start()

        // Zoom-in animasiyası
        binding.logoImage.scaleX = 0f
        binding.logoImage.scaleY = 0f
        val zoomIn = ObjectAnimator.ofFloat(binding.logoImage, "scaleX", 1f)
        val zoomInY = ObjectAnimator.ofFloat(binding.logoImage, "scaleY", 1f)
        zoomIn.duration = 1500
        zoomInY.duration = 1500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(zoomIn, zoomInY, glowAnimation)  // Birlikdə işləmək üçün
        animatorSet.start()

        // Ana ekrana keçid
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, ContainerActivity::class.java))
            finish()
        }, 1500)
    }
}
