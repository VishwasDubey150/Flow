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

