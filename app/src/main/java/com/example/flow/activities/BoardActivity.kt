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
    private var mselectedImageFileUri: Uri? = null

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
            if (mselectedImageFileUri != null)
            {
                uploadBoardImage()
            }
            else
            {
                showPB()
                createBoard()
            }
        }

        binding.boardBack.setOnClickListener {
            startActivity(Intent(this@BoardActivity,MainActivity::class.java))
        }

        binding.boardImg.setOnClickListener {
            Constants.showImageChooser(this)
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mselectedImageFileUri = data.data!!
                        Glide
                            .with(this)
                            .load(mselectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.baseline_person_24)
                            .into(binding.boardImg)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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


    private fun uploadBoardImage(){
        showPB()
        val sref : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE"+System.currentTimeMillis()
                        +"."+Constants.getfileExtension(this,mselectedImageFileUri))

        sref.putFile(mselectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
            Log.i("Board Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                Log.i("Downloadable Image URL",uri.toString())
                mBoardImageURL=uri.toString()
                createBoard()
            }
        }.addOnFailureListener {
                exception ->
            Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
            hidePB()
        }
    }


}