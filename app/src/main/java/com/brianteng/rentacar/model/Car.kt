package com.brianteng.rentacar.model

import android.os.Parcel
import android.os.Parcelable

data class Car(
    val id: Int,
    val name: String,
    val model: String,
    val year: Int,
    val rating: Float,
    val kilometers: Int,
    val dailyCost: Int,
    val imageResId: Int
) : Parcelable {

    // Manual Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(model)
        parcel.writeInt(year)
        parcel.writeFloat(rating)
        parcel.writeInt(kilometers)
        parcel.writeInt(dailyCost)
        parcel.writeInt(imageResId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car = Car(parcel)
        override fun newArray(size: Int): Array<Car?> = arrayOfNulls(size)
    }
}