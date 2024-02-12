package com.example.flow.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.flow.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doublebacktoexit = false
    private lateinit var mProgress : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showPB()
    {
        mProgress= Dialog(this)
        mProgress.setContentView(R.layout.dialog_progress)
        mProgress.show()
    }
    fun hidePB()
    {
        mProgress.dismiss()
    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit()
    {
        if(doublebacktoexit)
        {
            super.onBackPressed()
            return
        }
        this.doublebacktoexit=true

        Toast.makeText(this,"Please click back again to exit",Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            doublebacktoexit = false
        },2000)
    }

    fun showErrorSnackBar(message: String)
    {
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_SHORT)

        val snackbarView = snackBar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this,R.color.Red))
        snackBar.show()
    }
}