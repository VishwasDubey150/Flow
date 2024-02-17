package com.example.flow.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.flow.R
import com.example.flow.databinding.ActivityTaskListBinding
import firestore.FirestoreClass
import model.Board
import utils.Constants

class TaskListActivity : BaseActivity() {
    lateinit var binding: ActivityTaskListBinding

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

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showPB()
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }

    fun boardDetails(board: Board){
        hidePB()
        binding.tv.text=board.name
    }
}