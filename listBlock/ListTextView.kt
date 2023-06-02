package com.finch.design.text.listBlock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.finch.design.Theme
import com.finch.design.text.html.HtmlTextView

private const val CONTEXT_PADDING_START = 17

@Composable
fun ListTextView(
    modifier: Modifier = Modifier,
    prefix: String,
    text: AnnotatedString,
    nestingLevel: Int,
    style: TextStyle = TextStyle.Default,
    textColor: Color = Theme.colors.main,
    linkClicked: ((String) -> Unit)? = null,
) = Box(
    modifier = modifier,
) {
    val localDensity = LocalDensity.current
    val startPadding = 8.dp + CONTEXT_PADDING_START.dp * (nestingLevel - 1)
    var rowHeight by remember { mutableStateOf(0.dp) }

    Text(
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = startPadding)
            .height(rowHeight),
        text = prefix,
    )

    HtmlTextView(
        text = text,
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = startPadding + CONTEXT_PADDING_START.dp, end = 16.dp)
            .onGloballyPositioned { coordinates ->
                rowHeight = with(localDensity) { coordinates.size.height.toDp() }
            },
        style = style,
        color = textColor,
        linkClicked = linkClicked,
    )
}
