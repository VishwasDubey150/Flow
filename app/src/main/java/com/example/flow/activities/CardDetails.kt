package com.example.flow.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.flow.R
import com.example.flow.databinding.ActivityCardDetailsBinding
import com.example.flow.databinding.ActivityMembersBinding
import dialog.LabelColorListDialog
import firestore.FirestoreClass
import model.Board
import model.Card
import model.Task
import utils.Constants

class CardDetails : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskPosition = -1
    private var mCardPosition = -1

    private var mSelectedColor = ""

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
        binding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }
        binding.tv.text=mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name
        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
//        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString())

        mSelectedColor = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].labelColor

        if (mSelectedColor.isNotEmpty())
        {
            setColor()
        }

        binding.btnUpdateCardDetails.setOnClickListener {
            if(binding.etNameCardDetails.text.toString().isNotEmpty())
            {
                updateCArdDetails()
            }
            else{
                Toast.makeText(this@CardDetails,"Enter a card name",Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteBtn.setOnClickListener{
            alertDialogforDeleteCArd(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
        }
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

    fun addUpdateTaskListSuccess() {
        hidePB()
        setResult(Activity.RESULT_OK)
        startActivity(Intent(this@CardDetails,TaskListActivity::class.java))
        finish()
    }

    private fun updateCArdDetails(){
        val card = Card(binding.etNameCardDetails.text.toString(),
             mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo,
            mSelectedColor)


        mBoardDetails.taskList[mTaskPosition].cards[mCardPosition] = card
        showPB()
        FirestoreClass().addUpdateTaskList(this@CardDetails,mBoardDetails)
    }

    private fun deleteCard(){
        val cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskPosition].cards

        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskPosition].cards =cardList

        showPB()
        FirestoreClass().addUpdateTaskList(this@CardDetails,mBoardDetails)
    }

    private fun alertDialogforDeleteCArd(cardName : String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("Are you sure you want to delete card?")

        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            deleteCard()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorsList():ArrayList<String>
    {
        val colorList: ArrayList<String>  = ArrayList()
        colorList.add("#ffffff")
        colorList.add("#74E291")
        colorList.add("#F3B95F")

        return  colorList
    }

    private fun setColor()
    {
        binding.tvSelectLabelColor.text=""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog()
    {
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object :LabelColorListDialog(this, colorsList,"Select the label Color",mSelectedColor)
        {
            override fun onItemSelected(color: String) {
                mSelectedColor=color
                setColor()

            }
        }
        listDialog.show()
    }
}