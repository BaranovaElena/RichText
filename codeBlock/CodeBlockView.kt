package com.finch.design.text.codeBlock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun CodeBlockView(
    modifier: Modifier = Modifier,
    code: AnnotatedString,
    style: TextStyle = TextStyle.Default,
    textColor: Color = Theme.colors.main,
) = Row(
    modifier = modifier,
) {
    val localDensity = LocalDensity.current
    var rowHeight by remember { mutableStateOf(0.dp) }

    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(rowHeight)
            .background(Color(0xFF9686FE))
    )

    Text(
        text = code,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 10.dp)
            .onGloballyPositioned { coordinates ->
                rowHeight = with(localDensity) { coordinates.size.height.toDp() }
            },
        style = style,
        color = textColor,
    )
}
