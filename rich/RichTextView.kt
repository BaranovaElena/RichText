package com.finch.design.text.rich

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.finch.design.Theme
import com.finch.design.text.codeBlock.CodeBlockView
import com.finch.design.text.html.HtmlTextView
import com.finch.design.text.listBlock.ListTextView
import com.finch.design.text.rich.utils.RichTextViewData.CodeBlockViewData
import com.finch.design.text.rich.utils.RichTextViewData.HtmlTextViewData
import com.finch.design.text.rich.utils.RichTextViewData.ListTextViewData
import com.finch.design.text.rich.utils.fromWysiwyg
import com.finch.design.text.rich.utils.mapRichTextItems

/**
 * View for text that supports html text and custom views inside the text
 * (views for displaying code blocks, can be supplemented with other custom views)
 */
@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Theme.colors.main,
    linkClicked: ((String) -> Unit)? = null,
    fontSize: TextUnit = 14.sp,
    UrlSpanStyle: SpanStyle = SpanStyle(
        color = Theme.colors.textLink,
        textDecoration = TextDecoration.Underline
    )
) = Column(
    modifier = modifier,
) {

    mapRichTextItems(text.fromWysiwyg(), fontSize, UrlSpanStyle)
        .forEach { item ->
            when (item) {
                is HtmlTextViewData -> HtmlTextView(
                    text = item.text,
                    style = style,
                    color = color,
                    linkClicked = linkClicked,
                )
                is CodeBlockViewData -> CodeBlockView(
                    code = item.code,
                    style = style,
                    textColor = color,
                )
                is ListTextViewData -> ListTextView(
                    prefix = item.prefix,
                    text = item.text,
                    nestingLevel = item.nestingLevel,
                    style = style,
                    textColor = color,
                    linkClicked = linkClicked,
                )
            }
        }
}
