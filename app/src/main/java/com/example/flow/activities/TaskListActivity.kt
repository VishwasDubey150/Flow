package com.example.flow.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.Flow.adapters.TaskListItemsAdapter
import com.example.flow.R
import com.example.flow.databinding.ActivityTaskListBinding
import firestore.FirestoreClass
import model.Board
import model.Card
import model.Task
import model.User
import utils.Constants

class TaskListActivity : BaseActivity() {
    lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
            startActivityForResult(intent, MEMBER_REQUEST_CODE)
        }
        binding.more.setOnClickListener {

            val intent = Intent(this, MembersActivity::class.java)
            intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
            startActivity(intent)
            }

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showPB()
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBER_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
        {
            showPB()
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)
        }
        Log.e("cancel","cancel")
    }

    fun carDetails(taskListPosition: Int,cardPosition: Int)
    {
        val intent= Intent(this, CardDetails::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardDetails(board: Board) {

        mBoardDetails = board
        hidePB()
        binding.tv.text =board.name


        showPB()
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
    }

    fun addUpdateTaskListSuccess(){
        hidePB()
        showPB()
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName : String)
    {
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showPB()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun updateTaskList(position: Int, listName: String, model : Task){
        val task = Task(listName)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showPB()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int)
    {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
         showPB()
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun addCardToTaskList(position: Int,cardName: String)
    {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUserList)

        val cardsList = mBoardDetails.taskList[position].cards

        cardsList.add(card)
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList)
        mBoardDetails.taskList[position] = task

        showPB()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun boardMembersDetailsList(list: ArrayList<User>)
    {
        mAssignedMemberDetailList =list
        hidePB()
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this@TaskListActivity, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

    companion object{
        const val MEMBER_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }


}
