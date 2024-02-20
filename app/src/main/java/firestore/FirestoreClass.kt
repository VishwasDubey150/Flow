package firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.flow.activities.BoardActivity
import com.example.flow.activities.ProfileActivity
import com.example.flow.activities.MainActivity
import com.example.flow.activities.MembersActivity
import com.example.flow.activities.TaskListActivity
import com.example.flow.activities.login
import com.example.flow.activities.signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import model.Board
import model.User
import utils.Constants

open class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: signup, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun LoadUserData(activity: Activity, readBoardList: Boolean = false)
    {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is login ->{
                            activity.signInSuccess(loggedInUser)
                        }
                    is MainActivity ->{
                            activity.updateNAvigationUserDetails(loggedInUser,readBoardList)
                    }

                    is ProfileActivity ->{
                        activity.updateUserDetails(loggedInUser)
                    }
                }
            }

            .addOnFailureListener { e ->
                when(activity){
                    is login ->{
                        activity.hidePB()
                    }
                    is MainActivity ->{
                        activity.hidePB()
                    }
                    is ProfileActivity ->{
                        activity.hidePB()
                    }
                }
                Log.e("SignInUser","Error writing document",e)
            }
    }
    fun getCurrentUserId():String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null)
        {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()

                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardsListUI(boardList)

            }
    }

    fun updateuserProfileData(activity: ProfileActivity, userHashMap: HashMap<String,Any>)
    {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"profile updated Successfully!",Toast.LENGTH_SHORT).show()
                activity.profileupdateSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hidePB()
                Toast.makeText(activity,"Error in updating profile!",Toast.LENGTH_SHORT).show()
            }
    }

    fun createBoard(activity: BoardActivity , board: Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
                Toast.makeText(activity,"Board created Successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                activity.hidePB()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }


    fun addUpdateTaskList(activity: TaskListActivity,board: Board)
    {
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener {
                e ->
                activity.hidePB()
                Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
            }

    }

    fun getBoardDetails(activity: TaskListActivity,documented: String){
        mFirestore.collection(Constants.BOARDS)
            .document(documented)
            .get()
            .addOnSuccessListener {
                    document ->

                val board =document.toObject(Board::class.java)!!

                board.documentId=document.id
                activity.boardDetails(board)
                Log.i(activity.javaClass.simpleName, document.toString())
            }.addOnFailureListener {
                e ->
                activity.hidePB()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun getAssignedMembersListDetails(activity: MembersActivity,assignedTo:ArrayList<String>)
    {
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val userList : ArrayList<User> = ArrayList()

                for (i in document.documents)
                {
                    val user = i.toObject(User::class.java)!!
                    userList.add(user)
                }
                activity.setupMemberList(userList)
            }.addOnFailureListener {
                e->
                activity.hidePB()
                Log.e(activity.javaClass.simpleName,"Error",e)
            }
    }

    fun getMemberDetails(activity: MembersActivity,email:String)
    {
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document ->
                if (document.documents.size > 0)
                {
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else
                {
                    activity.hidePB()
                    activity.showErrorSnackBar("No Such mamber found")
                }
            }
            .addOnFailureListener {
                e->
                activity.hidePB()
                Log.e(activity.javaClass.simpleName,"error in getting user details",e)

            }
    }

    fun assignMemberToBoard(activity: MembersActivity,board: Board,user: User)
    {
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }.addOnFailureListener {
                e ->
                activity.hidePB()
                Log.e(activity.javaClass.simpleName,"error in getting user details",e)

            }
    }

}