package com.example.flow.activities

import adapter.MemberListItemsAdapter
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flow.R
import com.example.flow.databinding.ActivityMembersBinding
import firestore.FirestoreClass
import model.Board
import model.User
import utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDEtails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangesMade: Boolean = false
    lateinit var binding: ActivityMembersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }
        binding.btnBackMem.setOnClickListener {
            startActivity(Intent(this@MembersActivity, TaskListActivity::class.java))
        }

        if(intent.hasExtra(Constants.BOARD_DETAILS))
        {
            mBoardDEtails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
        }

        binding.addMember.setOnClickListener {
            dialogSearchMember()
        }
        showPB()
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDEtails.assignedTo)
    }

    fun setupMemberList(list: ArrayList<User>)
    {
        mAssignedMembersList = list

        hidePB()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this,list)
        binding.rvMembersList.adapter = adapter
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {

            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if(email.isNotEmpty())
            {
                dialog.dismiss()
                showPB()
                FirestoreClass().getMemberDetails(this,email)


            }
            else
            {
                Toast.makeText(this,"Enter the email address",Toast.LENGTH_SHORT).show()
            }

        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user: User)
    {
        mBoardDEtails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDEtails,user)
    }

    fun memberAssignSuccess(user: User)
    {
        hidePB()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setupMemberList(mAssignedMembersList)
    }

    override fun onBackPressed() {
        if(anyChangesMade)
        {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
}