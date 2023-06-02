package com.finch.design.text.html.utils

import android.graphics.Typeface
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.io.File

private const val PATH_SYSTEM_FONTS_FILE = "/system/etc/fonts.xml"
private const val PATH_SYSTEM_FONTS_DIR = "/system/fonts/"

internal fun UnderlineSpan.spanStyle(): SpanStyle =
    SpanStyle(textDecoration = TextDecoration.Underline)

internal fun ForegroundColorSpan.spanStyle(): SpanStyle =
    SpanStyle(color = Color(foregroundColor))

internal fun BackgroundColorSpan.spanStyle(): SpanStyle =
    SpanStyle(background = Color(backgroundColor))

internal fun StrikethroughSpan.spanStyle(): SpanStyle =
    SpanStyle(textDecoration = TextDecoration.LineThrough)

internal fun RelativeSizeSpan.spanStyle(fontSize: TextUnit): SpanStyle =
    SpanStyle(fontSize = (fontSize.value * sizeChange).sp)

internal fun StyleSpan.spanStyle(): SpanStyle? = when (style) {
    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
    Typeface.BOLD_ITALIC -> SpanStyle(
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
    )
    else -> null
}

internal fun SubscriptSpan.spanStyle(): SpanStyle =
    SpanStyle(baselineShift = BaselineShift.Subscript)

internal fun SuperscriptSpan.spanStyle(): SpanStyle =
    SpanStyle(baselineShift = BaselineShift.Superscript)

internal fun TypefaceSpan.spanStyle(): SpanStyle? {
    val xmlContent = File(PATH_SYSTEM_FONTS_FILE).readText()

    return if (xmlContent.contains("""<family name="$family""")) {
        val familyChunkXml = xmlContent.substringAfter("""<family name="$family""")
            .substringBefore("""</family>""")
        val fontName = familyChunkXml.substringAfter("""<font weight="400" style="normal">""")
            .substringBefore("</font>")

        SpanStyle(
            fontFamily = FontFamily(
                Typeface.createFromFile("$PATH_SYSTEM_FONTS_DIR$fontName")
            )
        )
    } else null
}
