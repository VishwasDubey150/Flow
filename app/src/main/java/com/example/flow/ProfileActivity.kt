package com.example.flow

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.flow.activities.BaseActivity
import com.example.flow.activities.MainActivity
import com.example.flow.databinding.ActivityProfileBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import firestore.FirestoreClass
import model.User
import utils.Constants
import java.io.IOException

class ProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION = 1
        private const val PICK_IMAGE_REQUEST_CODE = 1
    }


    private var mselectedImageFileUri: Uri? = null
    private lateinit var mUserDetails : User
    private var mProfileImageURL: String = ""

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }

        binding.profBack.setOnClickListener {
            startActivity(Intent(this@ProfileActivity,MainActivity::class.java))
        }

        binding.updImg.setOnClickListener {
            showImageChooser(this)

//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//            ) {
//                showImageChooser(this)
//            } else {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    READ_STORAGE_PERMISSION)
//            }
        }

        binding.btnUpdate.setOnClickListener {
            if(mselectedImageFileUri!=null)
            {
                uploadUserImage()
            }
            else{
                showPB()
                updateProfileData()
            }
        }

        FirestoreClass().LoadUserData(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this)

            } else {
                Toast.makeText(
                    this, "You Denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mselectedImageFileUri = data.data!!
                        Glide
                            .with(this)
                            .load(mselectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.baseline_person_24)
                            .into(binding.updImg)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun updateUserDetails(user: User) {
        val upd_img: ImageView = findViewById(R.id.upd_img)
        val upd_username: TextView = findViewById(R.id.upd_username)
        val upd_email: TextView = findViewById(R.id.upd_email)
        val upd_mob: TextView = findViewById(R.id.upd_mobile)

        mUserDetails=user

        upd_username.setText(user.name)
        upd_email.setText(user.email)
        upd_mob.setText(user.mobile.toString())


        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.baseline_person_24)
            .into(upd_img);
    }

    private fun uploadUserImage(){
        showPB()
        if(mselectedImageFileUri != null){
            val sref : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE"+System.currentTimeMillis()
                    +"."+getfileExtension(mselectedImageFileUri))

            sref.putFile(mselectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL=uri.toString()
                    updateProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(this@ProfileActivity,exception.message,Toast.LENGTH_SHORT).show()
                hidePB()
            }
        }
    }


    private fun getfileExtension(uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileupdateSuccess()
    {
        hidePB()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun updateProfileData(){
        val userHashMap = HashMap<String, Any>()
        var anyChanges = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image)
        {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChanges = true
        }

        if(binding.updUsername.text.toString() != mUserDetails.name)
        {
            userHashMap[Constants.NAME] = binding.updUsername.text.toString()
            anyChanges = true
        }

        if(binding.updMobile.text.toString() != mUserDetails.mobile.toString())
        {
            userHashMap[Constants.MOBILE] = binding.updMobile.text.toString().toLong()
            anyChanges = true
        }
        if (anyChanges){
            FirestoreClass().updateuserProfileData(this,userHashMap)
        }
    }

    fun TextView(view: View) {
        Toast.makeText(this,"You can't change email address!!",Toast.LENGTH_SHORT).show()
    }
}