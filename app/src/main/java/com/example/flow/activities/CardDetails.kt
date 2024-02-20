package com.example.flow.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flow.R
import com.example.flow.databinding.ActivityCardDetailsBinding
import com.example.flow.databinding.ActivityMembersBinding
import model.Board
import utils.Constants

class CardDetails : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskPosition = -1
    private var mCardPosition = -1

    lateinit var binding: ActivityCardDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCardDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        getIntentData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(android.R.color.black)
        }
        binding.tv.text=mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name
        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
//        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString())
    }

    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAILS))
        {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION))
        {
            mTaskPosition = intent.getIntExtra(
                Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION))
        {
            mCardPosition = intent.getIntExtra(
                Constants.CARD_LIST_ITEM_POSITION,-1
            )
        }
    }
}