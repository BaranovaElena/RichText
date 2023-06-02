package com.finch.design.text.codeBlock.utils

internal class CodeBlockSpan(
    language: String = "",
) : CodeTypeSpan(language) {

    override fun getSpanTypeId(): Int = CODE_BLOCK_SPAN_TYPE_ID

    companion object {
        private const val CODE_BLOCK_SPAN_TYPE_ID = 30
    }
}
