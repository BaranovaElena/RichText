package com.finch.design.text.listBlock.utils

import android.os.Parcel
import android.text.ParcelableSpan

internal class ListSpan(
    val leadingText: String = "",
    val nestingLevel: Int = 0,
) : ParcelableSpan {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(leadingText)
        dest?.writeInt(nestingLevel)
    }

    override fun getSpanTypeId(): Int = LIST_SPAN_TYPE_ID

    companion object {
        private const val LIST_SPAN_TYPE_ID = 31
    }
}
