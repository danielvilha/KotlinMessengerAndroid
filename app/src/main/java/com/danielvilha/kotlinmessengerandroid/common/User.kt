package com.danielvilha.kotlinmessengerandroid.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by danielvilha on 2019-06-26
 */
@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("", "", "")
}