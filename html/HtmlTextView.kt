package com.finch.design.text.html

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.finch.design.Theme
import com.finch.design.text.html.utils.URL_TAG

@Composable
fun HtmlTextView(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Theme.colors.main,
    linkClicked: ((String) -> Unit)? = null,
) {

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        style = style.copy(color = color),
        text = text,
        onClick = {
            text
                .getStringAnnotations(URL_TAG, it, it)
                .firstOrNull()
                ?.let { stringAnnotation ->
                    linkClicked?.let {
                        it(stringAnnotation.item)
                    } ?: uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}
