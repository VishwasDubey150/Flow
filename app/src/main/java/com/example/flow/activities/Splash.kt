package com.example.flow.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flow.R

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black) // Replace yourDesiredColor with the color you want
        }
        android.os.Handler().postDelayed({
            val mainIntent = Intent(this@splash, signup::class.java)
            startActivity(mainIntent)
            finish()
        }, 500)
    }
}