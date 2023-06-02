package com.finch.design.text.rich.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.finch.design.text.rich.utils.WysiwygTagHandler.Companion.DIV_TAG
import com.finch.design.text.rich.utils.WysiwygTagHandler.Companion.LIST_ITEM_TAG
import com.finch.design.text.rich.utils.WysiwygTagHandler.Companion.ORDERED_LIST_TAG
import com.finch.design.text.rich.utils.WysiwygTagHandler.Companion.UNORDERED_LIST_TAG

internal fun List<Any>.subListFromSpanned(spanned: Spanned, start: Int, end: Int) =
    this.filter {
        (spanned.getSpanStart(it) >= start) && (spanned.getSpanEnd(it) <= end)
    }

// Replace tags with new custom tags so that they wouldn't be handled by
// default implementation and would be forwarded to custom tag handler.
internal fun String.fromWysiwyg() = HtmlCompat.fromHtml(
    this.replace(oldValue = "\n", newValue = "<br>")
        .replace("<ul>", "<$UNORDERED_LIST_TAG>")
        .replace("</ul>", "</$UNORDERED_LIST_TAG>")
        .replace("<ol>", "<$ORDERED_LIST_TAG>")
        .replace("</ol>", "</$ORDERED_LIST_TAG>")
        .replace("<li>", "<$LIST_ITEM_TAG>")
        .replace("</li>", "</$LIST_ITEM_TAG>")
        .replace("<div", "<p><$DIV_TAG")
        .replace("</div>", "</$DIV_TAG></p>"),
    HtmlCompat.FROM_HTML_MODE_COMPACT,
    null,
    WysiwygTagHandler()
)
