package com.example.flow.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.flow.R
import com.example.flow.databinding.ActivityLoginBinding
import com.example.flow.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import model.User

class login : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth=FirebaseAuth.getInstance()
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }
        binding.lBtnLogin.setOnClickListener{
            loginUser()
        }
    }

    fun signInSuccess(user: User)
    {
        hidePB()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun loginUser(){
        val email: String = binding.lEtEmail.text.toString().trim(){it<=' '}
        val password: String = binding.lEtPassword.text.toString().trim(){it<=' '}

        if(validatelForm(email, password))
        {
            showPB()
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    task ->
                hidePB()
                if(task.isSuccessful){
                    Toast.makeText(this,"Logging in", Toast.LENGTH_SHORT).show()
//                        FirebaseAuth.getInstance().signOut()
                    val mainIntent = Intent(this@login, MainActivity::class.java)
                    startActivity(mainIntent)
                }
                else{
                    Toast.makeText(this,task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun validatelForm(l_email:String,l_password:String):Boolean {
        return when{
            TextUtils.isEmpty(l_email)->{
                showErrorSnackBar("Please enter your email")
                false
            }

            TextUtils.isEmpty(l_password)->{
                showErrorSnackBar("Please enter your password")
                false
            }
            else ->{
                true
            }
        }
    }



    fun go_to_signup(view: View) {
        val mainIntent = Intent(this@login, signup::class.java)
        startActivity(mainIntent)
    }
}