package com.finch.design.text.codeBlock.utils

import android.text.Spanned
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.finch.design.models.CodeConfigModel
import com.finch.design.models.CodeConfigModel.TextStyle.TypefaceStyle.BOLD
import com.finch.design.models.CodeConfigModel.TextStyle.TypefaceStyle.BOLD_ITALIC
import com.finch.design.models.CodeConfigModel.TextStyle.TypefaceStyle.ITALIC
import com.finch.design.models.CodeConfigModelDummy
import com.finch.design.text.html.utils.HandleHtml
import com.finch.design.text.rich.utils.RichTextViewData.CodeBlockViewData
import java.util.Locale

private const val QUOTES_REGEX = "\"(.+)\""
private const val WORD_DELIMITER_REGEX = "\\s+"
private const val COMMENT_REGEX = "//.*"

@Composable
internal fun mapCodeBlock(
    spans: List<Any>,
    spanned: Spanned,
    textStart: Int,
    textEnd: Int,
    fontSize: TextUnit,
    language: String,
) = CodeBlockViewData(
    code = buildAnnotatedString {
        val text = spanned.substring(textStart, textEnd)
        append(text)

        this.HandleHtml(spans, spanned, textStart, fontSize)
    },
    language = language,
)

@Composable
internal fun Builder.HandleCodeSyntax(text: String, language: String = "", offset: Int = 0) {

    val codeStyleMap = getLanguageDecoration(language)
        ?.syntaxStyle
        ?: mapOf()

    val words = text.split(WORD_DELIMITER_REGEX.toRegex())
    val wordsWithIndexes = getPairsWithIndexes(text, words, offset)

    wordsWithIndexes.forEach { pair ->
        codeStyleMap[pair.first]?.let { style ->
            this.AddCodeStyle(
                style = style,
                index = pair.second,
                length = pair.first.length,
            )
        }
    }
}

@Composable
internal fun Builder.HandleCodeCommented(text: String, language: String, offset: Int = 0) {

    val style = getLanguageDecoration(language)
        ?.commentedTextStyle
        ?: CodeConfigModel.TextStyle()

    val stringGroups = getStringListWithRegex(
        pattern = COMMENT_REGEX,
        text = text,
    )

    getPairsWithIndexes(text, stringGroups, offset).forEach { string ->
        this.AddCodeStyle(
            style = style,
            index = string.second,
            length = string.first.length,
        )
    }
}

@Composable
internal fun Builder.HandleCodeTextValue(text: String, language: String, offset: Int = 0) {

    val stringGroups = getStringListWithRegex(
        pattern = QUOTES_REGEX,
        text = text,
    )

    val style = getLanguageDecoration(language)
        ?.textInQuotesStyle
        ?: CodeConfigModel.TextStyle()

    getPairsWithIndexes(text, stringGroups, offset).forEach { string ->
        this.AddCodeStyle(
            style = style,
            index = string.second,
            length = string.first.length,
        )
    }
}

@Composable
private fun Builder.AddCodeStyle(style: CodeConfigModel.TextStyle, index: Int, length: Int) {
    addStyle(
        style = SpanStyle(
            color = Color(style.color),
            fontWeight = when (style.typeface) {
                BOLD, BOLD_ITALIC -> FontWeight.Bold
                else -> FontWeight.Normal
            },
            fontStyle = when (style.typeface) {
                ITALIC, BOLD_ITALIC -> FontStyle.Italic
                else -> FontStyle.Normal
            }
        ),
        start = index,
        end = index + length,
    )
}

private fun getPairsWithIndexes(text: String, words: List<String>, offset: Int) =
    mutableListOf<Pair<String, Int>>().apply {
        var startIndex = 0

        words.forEach { word ->
            val index = text.indexOf(word, startIndex, ignoreCase = true)
            if (index >= 0) {
                add(Pair(word, index + offset))
                startIndex = index + 1
            }
        }
    }

@Composable
private fun getLanguageDecoration(language: String) =
    CodeConfigModelDummy.model.themeConfig.get(
        when (isSystemInDarkTheme()) {
            true -> "dark"
            false -> "light"
        }
    )
        ?.languageConfig
        ?.get(language.lowercase(Locale.getDefault()))

private fun getStringListWithRegex(pattern: String, text: String) =
    pattern.toRegex()
        .findAll(text)
        .toList()
        .map { it.value }
