package com.finch.design.text.codeBlock.utils

internal class CodeSpan(
    language: String = "",
) : CodeTypeSpan(language) {

    override fun getSpanTypeId(): Int = CODE_SPAN_TYPE_ID

    companion object {
        private const val CODE_SPAN_TYPE_ID = 31
    }
}
