package com.finch.design.text.codeBlock.utils

import android.os.Parcel
import android.text.ParcelableSpan

internal abstract class CodeTypeSpan(
    val language: String = "",
) : ParcelableSpan {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(language)
    }
}
