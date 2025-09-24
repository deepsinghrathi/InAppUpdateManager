package com.corapana.realtimeupdater

import android.os.Parcel
import android.os.Parcelable

data class UpdateInfo(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val whatsNew: String,
    val apkUrl: String,
    val forceUpdate: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        latestVersionCode = parcel.readInt(),
        latestVersionName = parcel.readString().orEmpty(),
        whatsNew = parcel.readString().orEmpty(),
        apkUrl = parcel.readString().orEmpty(),
        forceUpdate = parcel.readByte().toInt() != 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(latestVersionCode)
        parcel.writeString(latestVersionName)
        parcel.writeString(whatsNew)
        parcel.writeString(apkUrl)
        parcel.writeByte(if (forceUpdate) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UpdateInfo> {
        override fun createFromParcel(parcel: Parcel): UpdateInfo = UpdateInfo(parcel)
        override fun newArray(size: Int): Array<UpdateInfo?> = arrayOfNulls(size)
    }
}



