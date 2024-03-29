package utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.flow.activities.ProfileActivity

object Constants {
    const val USERS:String="users"

    const val BOARDS: String ="boards"

    const val IMAGE:String="image"
    const val NAME:String="name"
    const val ASSIGNED_TO : String = "assignedTo"
    const val MOBILE:String="mobile"
    const val READ_STORAGE_PERMISSION = 1
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAILS: String = "board_detail"
    const val ID: String = "id"
    const val EMAIL: String ="email"
    const val BOARD_MEMBERS_LIST: String ="board_member_list"
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"
    const val SELECT: String="Select"
    const val UN_SELECT: String="UnSelect"
    const val FLOW_PREFERENCES = "flowprefs"

    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN="fcmToken"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getfileExtension(activity : Activity,uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}

