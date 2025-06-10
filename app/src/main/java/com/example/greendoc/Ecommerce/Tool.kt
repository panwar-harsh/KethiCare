import android.os.Parcel
import android.os.Parcelable

data class Tool(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val price: String = "",
    val negotiable: Boolean = false,
    val contact: String = "",
    val imageUrl: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(price)
        parcel.writeByte(if (negotiable) 1 else 0)
        parcel.writeString(contact)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Tool> {
        override fun createFromParcel(parcel: Parcel): Tool {
            return Tool(parcel)
        }

        override fun newArray(size: Int): Array<Tool?> {
            return arrayOfNulls(size)
        }
    }
}
