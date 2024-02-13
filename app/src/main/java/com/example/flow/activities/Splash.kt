package com.example.flow.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flow.R
import firestore.FirestoreClass

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black) // Replace yourDesiredColor with the color you want
        }
        var currentUSerID = FirestoreClass().getCurrentUserId()
        android.os.Handler().postDelayed({
            if(currentUSerID.isNotEmpty())
            {
                startActivity(Intent(this@splash,MainActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this@splash,login::class.java))
                finish()
            }
        }, 500)
    }
}