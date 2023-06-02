package com.finch.design.text.rich.utils

import android.text.Editable
import android.text.Html.TagHandler
import android.text.Spannable
import android.text.Spanned
import com.finch.design.text.codeBlock.utils.CodeBlockSpan
import com.finch.design.text.codeBlock.utils.CodeSpan
import com.finch.design.text.listBlock.utils.ListSpan
import com.finch.design.text.rich.utils.WysiwygTagHandler.MarkerSpan.Bullet
import com.finch.design.text.rich.utils.WysiwygTagHandler.MarkerSpan.Code
import com.finch.design.text.rich.utils.WysiwygTagHandler.MarkerSpan.Number
import com.finch.design.text.rich.utils.WysiwygTagHandler.Tag.Ordered
import com.finch.design.text.rich.utils.WysiwygTagHandler.Tag.Unordered
import org.xml.sax.XMLReader
import java.util.LinkedList

internal class WysiwygTagHandler : TagHandler {
    private val attributes = mutableMapOf<String, String>()
    private val parentList: LinkedList<Tag> = LinkedList()
    private var currentListNestingLevel: Int = 0
    private var isCodeInBlock: Boolean = false

    /**
     * Marker interface for spans that will be replaced later with actual styling.
     * Subclasses should be classes not objects, for correct ordering returned from [getLast].
     */
    private interface MarkerSpan {
        class Bullet : MarkerSpan
        class Number : MarkerSpan
        class Code : MarkerSpan
        class CodeBlock : MarkerSpan
    }

    /**
     * Interface for handling list tags.
     */
    private sealed interface Tag {
        fun openTag(text: Editable)
        fun closeTag(text: Editable, nestingLevel: Int = 0, attrs: Map<String, String>? = null)

        object Code : Tag {
            override fun openTag(text: Editable) {
                start(text, Code())
            }

            override fun closeTag(text: Editable, nestingLevel: Int, attrs: Map<String, String>?) {
                end<MarkerSpan.Code>(text, getSpans(attrs))
            }

            private fun getSpans(attrs: Map<String, String>?): List<Any> {
                return CodeSpan(
                    language = attrs?.get(CODE_LANGUAGE_ATTR).orEmpty()
                ).let(::listOf)
            }
        }

        object CodeBlock : Tag {
            override fun openTag(text: Editable) {
                start(text, MarkerSpan.CodeBlock())
            }

            override fun closeTag(text: Editable, nestingLevel: Int, attrs: Map<String, String>?) {
                end<MarkerSpan.CodeBlock>(text, getSpans(attrs))
            }

            private fun getSpans(attrs: Map<String, String>?): List<Any> {
                return CodeBlockSpan(
                    language = attrs?.get(CODE_LANGUAGE_ATTR).orEmpty()
                ).let(::listOf)
            }
        }

        object Unordered : Tag {
            override fun openTag(text: Editable) {
                start(text, Bullet())
            }

            override fun closeTag(text: Editable, nestingLevel: Int, attrs: Map<String, String>?) {
                end<Bullet>(text, getSpans(nestingLevel))
            }

            private fun getSpans(nestingLevel: Int) = listOf(
                ListSpan(leadingText = "• ", nestingLevel = nestingLevel)
            )
        }

        class Ordered : Tag {
            private var index = 0

            override fun openTag(text: Editable) {
                start(text, Number())
                index++
            }

            override fun closeTag(text: Editable, nestingLevel: Int, attrs: Map<String, String>?) {
                end<Number>(text, getSpans(nestingLevel))
            }

            private fun getSpans(nestingLevel: Int) = listOf(
                ListSpan(leadingText = "$index. ", nestingLevel = nestingLevel)
            )
        }
    }

    private fun processAttributes(xmlReader: XMLReader) = kotlin.runCatching {
        val element = xmlReader.javaClass
            .getDeclaredField("theNewElement")
            .apply { isAccessible = true }
            .get(xmlReader)

        val atts = element.javaClass
            .getDeclaredField("theAtts")
            .apply { isAccessible = true }
            .get(element)

        val data = atts.javaClass
            .getDeclaredField("data")
            .apply { isAccessible = true }
            .get(atts) as? Array<String> ?: arrayOf()

        val length = atts.javaClass
            .getDeclaredField("length")
            .apply { isAccessible = true }
            .get(atts) as? Int ?: 0

        for (i in 0..length) {
            attributes[data[i * 5 + 1]] = data[i * 5 + 4]
        }
    }

    override fun handleTag(
        opening: Boolean,
        tag: String,
        output: Editable,
        xmlReader: XMLReader?
    ) {
        xmlReader?.let { processAttributes(xmlReader) }

        when (tag) {
            DIV_TAG -> {
                if (attributes[CLASS_ATTR].orEmpty() == CODE_BLOCK_ATTR_VALUE) {
                    isCodeInBlock = opening
                }
            }
            CODE_TAG -> when {
                opening && isCodeInBlock -> Tag.CodeBlock.openTag(output)
                opening && !isCodeInBlock -> Tag.Code.openTag(output)
                !opening && isCodeInBlock -> Tag.CodeBlock.closeTag(text = output, attrs = attributes)
                else -> Tag.Code.closeTag(text = output, attrs = attributes)
            }
            UNORDERED_LIST_TAG -> {
                if (opening) {
                    parentList.push(Unordered)
                    currentListNestingLevel++
                } else {
                    parentList.pop()
                    currentListNestingLevel--
                }
            }
            ORDERED_LIST_TAG -> {
                if (opening) {
                    parentList.push(Ordered())
                    currentListNestingLevel++
                } else {
                    parentList.pop()
                    currentListNestingLevel--
                }
            }
            LIST_ITEM_TAG -> {
                if (opening) {
                    parentList.peek()?.openTag(output)
                } else {
                    parentList.peek()?.closeTag(output, nestingLevel = currentListNestingLevel)
                }
            }
        }
    }

    companion object {
        const val UNORDERED_LIST_TAG = "unorderedList"
        const val ORDERED_LIST_TAG = "orderedList"
        const val LIST_ITEM_TAG = "listItem"
        const val DIV_TAG = "divTag"

        private const val CODE_TAG = "code"
        private const val CODE_LANGUAGE_ATTR = "data-language"
        private const val CLASS_ATTR = "class"
        private const val CODE_BLOCK_ATTR_VALUE = "toastui-editor-ww-code-block"

        private fun start(
            text: Editable,
            mark: MarkerSpan,
        ) {
            val len = text.length
            text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK)
        }

        /**
         * Replaces marker span with actual styling span.
         */
        private inline fun <reified T : MarkerSpan> end(
            text: Editable,
            replacementSpans: List<Any>
        ) {
            getLast<T>(text)?.let { markerSpan ->
                replacementSpans.forEach { span ->
                    setSpanFromMark(text = text, mark = markerSpan, span = span)
                }
                text.removeSpan(markerSpan)
            }
        }

        /**
         * This knows that the last returned object from [Spanned.getSpans] will be the most recently added.
         */
        private inline fun <reified T : MarkerSpan> getLast(text: Spanned): MarkerSpan? {
            return text.getSpans(0, text.length, T::class.java).lastOrNull()
        }

        private fun setSpanFromMark(text: Spannable, mark: MarkerSpan, span: Any) {
            val where = text.getSpanStart(mark)
            val len = text.length
            if (where != len) {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}
