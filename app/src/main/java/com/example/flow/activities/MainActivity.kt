package com.example.flow.activities
import adapter.BoardItemsAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.flow.R
import com.example.flow.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import firestore.FirestoreClass
import model.Board
import model.User
import utils.Constants

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }
    private lateinit var mUSerName: String

    private lateinit var mSharedPreferences: SharedPreferences

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED , false)
//
//        if(tokenUpdated){
//            showPB()
//            FirestoreClass().LoadUserData(this,true)
//        }
//        else{
//            FirebaseMessaging.getInstance().token.addOnSuccessListener(this@MainActivity){
//                result ->
//                updateFCMToken(result)
//            }
//        }

        mSharedPreferences = this.getSharedPreferences(Constants.FLOW_PREFERENCES,Context.MODE_PRIVATE)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }

        binding.appbar.mainContent.btnMenu.setOnClickListener {
            toggleDrawer()
        }

        binding.navView.setNavigationItemSelectedListener(this)
        showPB()
        FirestoreClass().LoadUserData(this,true)

        binding.appbar.floatingBtn.setOnClickListener {
            val intent = Intent(this@MainActivity,BoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUSerName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode ==  Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE)
        {
            FirestoreClass().LoadUserData(this)
        }

        else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }


        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun toggleDrawer(){
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else
        {
            doubleBackToExit()
        }
    }
    fun updateNAvigationUserDetails(user: User,readBoardList: Boolean) {

        val nav_user_img:ImageView = findViewById(R.id.nav_user_img)
        val nav_username: TextView = findViewById(R.id.nav_username)

        mUSerName =user.name

        nav_username.text=user.name

        if (readBoardList)
        {
            FirestoreClass().getBoardsList(this)
        }

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.baseline_person_24)
            .into(nav_user_img);
    }

    fun populateBoardsListUI(boardsList: ArrayList<Board>)
    {
        hidePB()
        if(boardsList.size>0){
            binding.appbar.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.appbar.mainContent.tvNoBoard.visibility = View.GONE

            binding.appbar.mainContent.rvBoardsList.layoutManager =LinearLayoutManager(this)
            binding.appbar.mainContent.rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            binding.appbar.mainContent.rvBoardsList.adapter = adapter

            adapter.setOnCLickListener(object :BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }
        else{
            binding.appbar.mainContent.rvBoardsList.visibility =View.GONE
            binding.appbar.mainContent.tvNoBoard.visibility =View.VISIBLE
        }
    }

    fun tokenUpdateSuccess(){
        hidePB()
        val editor:SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showPB()
        FirestoreClass().LoadUserData(this,true)
    }

    private fun updateFCMToken(token: String)
    {
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showPB()
        FirestoreClass().updateuserProfileData(this,userHashMap)
    }
}