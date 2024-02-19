package com.example.flow.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.Flow.adapters.TaskListItemsAdapter
import com.example.flow.R
import com.example.flow.databinding.ActivityTaskListBinding
import firestore.FirestoreClass
import model.Board
import model.Card
import model.Task
import utils.Constants

class TaskListActivity : BaseActivity() {
    lateinit var binding: ActivityTaskListBinding

    private lateinit var mBoardDetails : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Toast.makeText(this,"Tasks",Toast.LENGTH_SHORT).show()
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this@TaskListActivity,MainActivity::class.java))
        }

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showPB()
        FirestoreClass().getBoardDetails(this,boardDocumentId)

    }

    fun boardDetails(board: Board) {

        mBoardDetails = board
        hidePB()
        binding.tv.text =board.name

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this@TaskListActivity, board.taskList)
        binding.rvTaskList.adapter = adapter
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
}
