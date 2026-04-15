package com.example.akashacrudapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var txtAppName: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        imgLogo = findViewById(R.id.imgLogo)
        txtAppName = findViewById(R.id.txtAppName)
        txtSubtitle = findViewById(R.id.txtSubtitle)
        btnStart = findViewById(R.id.btnStart)

        startSplashAnimation()

        btnStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun startSplashAnimation() {
        imgLogo.translationY = 30f
        txtAppName.translationY = 20f
        txtSubtitle.translationY = 20f
        btnStart.translationY = 20f

        imgLogo.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .start()

        txtAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(180)
            .setDuration(600)
            .start()

        txtSubtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(320)
            .setDuration(600)
            .start()

        btnStart.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(450)
            .setDuration(600)
            .start()
    }
}