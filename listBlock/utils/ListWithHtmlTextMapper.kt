package com.finch.design.text.listBlock.utils

import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import com.finch.design.text.html.utils.HandleHtml
import com.finch.design.text.rich.utils.RichTextViewData.ListTextViewData

@Composable
internal fun mapListWithHtml(
    spans: List<Any>,
    spanned: Spanned,
    textStart: Int,
    textEnd: Int,
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle,
    leadingText: String,
    nestingLevel: Int,
) = ListTextViewData(
    text = buildAnnotatedString {
        val text = spanned.substring(textStart, textEnd)
        append(text)

        this.HandleHtml(spans, spanned, textStart, fontSize, urlSpanStyle)
    },
    prefix = leadingText,
    nestingLevel = nestingLevel,
)
