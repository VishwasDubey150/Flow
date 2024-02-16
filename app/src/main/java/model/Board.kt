package model

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name : String = "",
    val image : String = "",
    val createdBy : String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p: Parcel, p1: Int) = with(p) {
        p.writeString(name)
        p.writeString(image)
        p.writeString(createdBy)
        p.writeStringList(assignedTo)
        p.writeString(documentId)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
