package com.example.flow.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.flow.R
import com.example.flow.databinding.ActivityBoardBinding
import com.example.flow.databinding.ActivityProfileBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import firestore.FirestoreClass
import model.Board
import utils.Constants
import java.io.IOException

class BoardActivity : BaseActivity() {

    lateinit var binding: ActivityBoardBinding

    private  lateinit var mUserName :  String

    private var mBoardImageURL : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding.btnCreateBoard.setOnClickListener {

                showPB()
                createBoard()
            }


        binding.boardBack.setOnClickListener {
            startActivity(Intent(this@BoardActivity,MainActivity::class.java))
        }

//        binding.boardImg.setOnClickListener {
//            Constants.showImageChooser(this)
//        }
    }

    fun boardCreatedSuccessfully()
    {
        hidePB()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        var board = Board(
            binding.etBoardname.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )
        FirestoreClass().createBoard(this, board)
    }
}