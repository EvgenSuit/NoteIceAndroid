package com.suit.noteice.utils.ui

import android.content.Context
import androidx.annotation.StringRes
import kotlin.properties.Delegates

sealed class UIText {

    data class StringResource(@StringRes val id: Int, val args: List<Any> = emptyList()) : UIText()
    data class StringValue(val string: String): UIText()

    fun toString(context: Context) =
        when (this) {
            is StringResource -> context.getString(id, *args.toTypedArray())
            is StringValue -> string
        }
}
