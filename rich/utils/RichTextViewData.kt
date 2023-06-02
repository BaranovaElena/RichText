package com.finch.design.text.rich.utils

import androidx.compose.ui.text.AnnotatedString

internal sealed class RichTextViewData {

    data class HtmlTextViewData(
        val text: AnnotatedString,
    ) : RichTextViewData()

    data class CodeBlockViewData(
        val code: AnnotatedString,
        val language: String,
    ) : RichTextViewData()

    data class ListTextViewData(
        val text: AnnotatedString,
        val prefix: String,
        val nestingLevel: Int,
    ) : RichTextViewData()
}
