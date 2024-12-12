package com.android.gustyx.officedesk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val logoImageView = findViewById<ImageView>(R.id.logoImageView)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoImageView.startAnimation(fadeInAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InlineActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
