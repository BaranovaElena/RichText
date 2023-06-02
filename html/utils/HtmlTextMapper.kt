package com.finch.design.text.html.utils

import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import com.finch.design.text.codeBlock.utils.CodeTypeSpan
import com.finch.design.text.codeBlock.utils.HandleCodeCommented
import com.finch.design.text.codeBlock.utils.HandleCodeSyntax
import com.finch.design.text.codeBlock.utils.HandleCodeTextValue
import com.finch.design.text.rich.utils.RichTextViewData.HtmlTextViewData

internal const val URL_TAG = "url_tag"

@Composable
internal fun mapHtmlText(
    spans: List<Any>,
    spanned: Spanned,
    textStart: Int,
    textEnd: Int,
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle,
) = HtmlTextViewData(
    text = buildAnnotatedString {
        append(spanned.substring(textStart, textEnd))

        this.HandleHtml(spans, spanned, textStart, fontSize, urlSpanStyle)
    }
)

@Composable
internal fun Builder.HandleHtml(
    spans: List<Any>,
    spanned: Spanned,
    textStart: Int,
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle? = null,
) {
    spans.forEach { span ->
        val spanStart = spanned.getSpanStart(span)
        val spanEnd = spanned.getSpanEnd(span)

        val start = spanStart - textStart
        val end = spanEnd - textStart

        urlSpanStyle?.let {
            if (span is URLSpan) {
                addStringAnnotation(
                    tag = URL_TAG,
                    annotation = span.url,
                    start = start,
                    end = end
                )
            }
        }

        getTextSpanStyle(span, fontSize, urlSpanStyle)?.let { style ->
            addStyle(style, start, end)
        }

        if (span is CodeTypeSpan) {
            val spanText = spanned.substring(spanStart, spanEnd)

            this.HandleCodeSyntax(spanText, offset = start)
            this.HandleCodeTextValue(spanText, span.language, offset = start)
            this.HandleCodeCommented(spanText, span.language, offset = start)
        }
    }
}

private fun getTextSpanStyle(span: Any, fontSize: TextUnit, urlSpanStyle: SpanStyle?): SpanStyle? =
    when (span) {
        is RelativeSizeSpan -> span.spanStyle(fontSize)
        is StyleSpan -> span.spanStyle()
        is UnderlineSpan -> span.spanStyle()
        is ForegroundColorSpan -> span.spanStyle()
        is BackgroundColorSpan -> span.spanStyle()
        is TypefaceSpan -> span.spanStyle()
        is StrikethroughSpan -> span.spanStyle()
        is SuperscriptSpan -> span.spanStyle()
        is SubscriptSpan -> span.spanStyle()
        is URLSpan -> urlSpanStyle
        else -> null
    }
