package com.example.flow.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.flow.R
import com.example.flow.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import firestore.FirestoreClass
import model.User

class signup : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black) // Replace yourDesiredColor with the color you want
        }
        binding.btnSignup.setOnClickListener {
            registerUser()
            }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"Resgistered",Toast.LENGTH_SHORT).show()
        hidePB()
    }

    private fun registerUser(){
        val name: String = binding.etUsername.text.toString().trim(){it<=' '}
        val email: String = binding.etEmail.text.toString().trim(){it<=' '}
        val password: String = binding.etPassword.text.toString().trim(){it<=' '}

        if(validateForm(name,email, password))
        {
            showPB()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult> {
                    task ->
                    if(task.isSuccessful){
                        val firebaseUser:FirebaseUser = task.result!!.user!!
                        val registerEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registerEmail)
                        FirestoreClass().registerUser(this@signup,user)
//                          FirebaseAuth.getInstance().signOut()
                        val mainIntent = Intent(this@signup, login::class.java)
                        startActivity(mainIntent)
                    }
                    else{
                        Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    private fun validateForm(name :String, email:String,password:String):Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter your name")
                false
            }

            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter your email")
                false
            }

            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter your password")
                false
            }
            else ->{
                true
            }
        }
    }

    fun go_to_login(view: View) {
        val mainIntent = Intent(this@signup, login::class.java)
        startActivity(mainIntent)
    }
}