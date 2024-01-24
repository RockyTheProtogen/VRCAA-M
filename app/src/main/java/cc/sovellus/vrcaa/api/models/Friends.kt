package cc.sovellus.vrcaa.api.models


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Friends() : ArrayList<Friends.FriendsItem>(), Parcelable {
    constructor(parcel: Parcel) : this() {}

    data class FriendsItem(
        @SerializedName("bio")
        val bio: String,
        @SerializedName("bioLinks")
        val bioLinks: List<String>?,
        @SerializedName("currentAvatarImageUrl")
        val currentAvatarImageUrl: String,
        @SerializedName("currentAvatarTags")
        val currentAvatarTags: List<String>,
        @SerializedName("currentAvatarThumbnailImageUrl")
        val currentAvatarThumbnailImageUrl: String,
        @SerializedName("developerType")
        val developerType: String,
        @SerializedName("displayName")
        val displayName: String,
        @SerializedName("friendKey")
        val friendKey: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("isFriend")
        val isFriend: Boolean,
        @SerializedName("last_login")
        val lastLogin: String,
        @SerializedName("last_platform")
        val lastPlatform: String,
        @SerializedName("location")
        var location: String,
        @SerializedName("profilePicOverride")
        val profilePicOverride: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("statusDescription")
        val statusDescription: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("userIcon")
        val userIcon: String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Friends> {
        override fun createFromParcel(parcel: Parcel): Friends {
            return Friends(parcel)
        }

        override fun newArray(size: Int): Array<Friends?> {
            return arrayOfNulls(size)
        }
    }
}