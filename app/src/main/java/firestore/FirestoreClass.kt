package firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.flow.ProfileActivity
import com.example.flow.activities.MainActivity
import com.example.flow.activities.login
import com.example.flow.activities.signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    fun LoadUserData(activity: Activity)
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
                            activity.updateNAvigationUserDetails(loggedInUser)
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

    fun updateuserProfileData(activity: ProfileActivity,userHashMap: HashMap<String,Any>)
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
}