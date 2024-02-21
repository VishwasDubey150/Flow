package com.example.flow.activities

import adapter.CardMemberListItemsAdapter
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flow.R
import com.example.flow.databinding.ActivityCardDetailsBinding
import com.example.flow.databinding.ActivityMembersBinding
import dialog.LabelColorListDialog
import dialog.MembersListDialog
import firestore.FirestoreClass
import model.Board
import model.Card
import model.SelectedMembers
import model.Task
import model.User
import utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CardDetails : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskPosition = -1
    private var mCardPosition = -1

    private lateinit var mMembersDetailList: ArrayList<User>
    private var mSelectedColor = ""

    lateinit var binding: ActivityCardDetailsBinding

    private var mSelectedDueDateMilli:Long = 0

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
        binding.btnBackForCarddetails.setOnClickListener{
            startActivity(Intent(this@CardDetails,MainActivity::class.java))
        }
        binding.tv.text=mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name
        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
//        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString())

        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }
        binding.tvSelectDueDate.setOnClickListener {
            showDataPicker()
        }
        mSelectedColor = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].labelColor

        if (mSelectedColor.isNotEmpty())
        {
            setColor()
        }

        mSelectedDueDateMilli= mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].dueDate

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val selectedDate = simpleDateFormat.format(mSelectedDueDateMilli)

        binding.tvSelectDueDate.text = selectedDate

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
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST))
        {
            mMembersDetailList = intent.getParcelableArrayListExtra(
                Constants.BOARD_MEMBERS_LIST
            )!!
        }
    }

    fun addUpdateTaskListSuccess() {
        hidePB()
        setResult(Activity.RESULT_OK)
        startActivity(Intent(this@CardDetails,MainActivity::class.java))
        finish()
    }

    private fun updateCArdDetails(){
        val card = Card(binding.etNameCardDetails.text.toString(),
             mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,mSelectedDueDateMilli)


        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
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
        colorList.add("#416D19")
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

        val listDialog = object :LabelColorListDialog(this, colorsList,"Select the Color (white:Not Started , Green : Completed , yellow : In Progress)",mSelectedColor)
        {
            override fun onItemSelected(color: String) {
                mSelectedColor=color
                setColor()

            }
        }
        listDialog.show()
    }

    private fun membersListDialog() {
        var cardAssignedMembesList =
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembesList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembesList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(this, mMembersDetailList, "Select Member") {
            override fun onItemSelected(user: User, action: String) {

                if (action == Constants.SELECT)
                {
                    if (!mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.contains(user.id))
                    {
                        mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }
                else
                {
                    mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo.remove(user.id)
                    for (i in mMembersDetailList.indices)
                    {
                        if (mMembersDetailList[i].id == user.id)
                        {
                            mMembersDetailList[i].selected = false
                        }
                    }

                }
                setupSelectedMembersList()
            }
            }
        listDialog.show()
        }

    private fun setupSelectedMembersList() {

        val cardAssignedMembersList =mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo

        // A instance of selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {

            selectedMembersList.add(SelectedMembers("", ""))

            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this@CardDetails, 6)
            val adapter = CardMemberListItemsAdapter(this@CardDetails, selectedMembersList,true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(object :
                CardMemberListItemsAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }

    private fun showDataPicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        val dpd = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
               val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding.tvSelectDueDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilli= theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

}