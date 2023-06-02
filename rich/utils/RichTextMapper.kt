package com.finch.design.text.rich.utils

import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import com.finch.design.text.codeBlock.utils.CodeBlockSpan
import com.finch.design.text.codeBlock.utils.mapCodeBlock
import com.finch.design.text.html.utils.mapHtmlText
import com.finch.design.text.listBlock.utils.ListSpan
import com.finch.design.text.listBlock.utils.mapListWithHtml

@Composable
internal fun mapRichTextItems(
    spanned: Spanned,
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle,
): List<RichTextViewData> {

    val spans = spanned
        .getSpans(0, spanned.length, Any::class.java)
        .filter { it !is Unit }
        .sortedBy { spanned.getSpanStart(it) }

    val viewDataList = mutableListOf<RichTextViewData>()
    var startIndex = 0

    spans.forEach { span ->
        when (span) {
            is CodeBlockSpan -> {
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)

                if (start > startIndex)
                    viewDataList.add(
                        mapHtmlText(
                            spans = spans.subListFromSpanned(spanned, startIndex, start),
                            spanned = spanned,
                            textStart = startIndex,
                            textEnd = start,
                            fontSize = fontSize,
                            urlSpanStyle = urlSpanStyle,
                        )
                    )

                viewDataList.add(
                    mapCodeBlock(
                        spans = spans.subListFromSpanned(spanned, start, end),
                        spanned = spanned,
                        textStart = start,
                        textEnd = end,
                        fontSize = fontSize,
                        language = span.language,
                    )
                )

                startIndex = end
            }
            is ListSpan -> {
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)

                val listSpans = spans
                    .subListFromSpanned(spanned, start, end)
                    .filterIsInstance<ListSpan>()
                val listTextEnd = if (listSpans.size > 1) {
                    spanned.getSpanStart(listSpans[1])
                } else end

                if (start > startIndex)
                    viewDataList.add(
                        mapHtmlText(
                            spans = spans.subListFromSpanned(spanned, startIndex, start),
                            spanned = spanned,
                            textStart = startIndex,
                            textEnd = start,
                            fontSize = fontSize,
                            urlSpanStyle = urlSpanStyle,
                        )
                    )

                viewDataList.add(
                    mapListWithHtml(
                        spans = spans.subListFromSpanned(spanned, start, listTextEnd),
                        spanned = spanned,
                        textStart = start,
                        textEnd = listTextEnd,
                        fontSize = fontSize,
                        urlSpanStyle = urlSpanStyle,
                        leadingText = span.leadingText,
                        nestingLevel = span.nestingLevel,
                    )
                )

                startIndex = end
            }
        }
    }

    if (startIndex < spanned.lastIndex) {
        viewDataList.add(
            mapHtmlText(
                spans = spans.subListFromSpanned(spanned, startIndex, spanned.lastIndex),
                spanned = spanned,
                textStart = startIndex,
                textEnd = spanned.lastIndex,
                fontSize = fontSize,
                urlSpanStyle = urlSpanStyle,
            )
        )
    }

    return viewDataList
}
