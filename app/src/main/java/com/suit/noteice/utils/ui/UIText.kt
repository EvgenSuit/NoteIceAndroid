package com.suit.noteice.utils.ui

import android.content.Context
import androidx.annotation.StringRes
import kotlin.properties.Delegates

sealed class UIText {

    data class StringResource(@StringRes val id: Int, val args: Array<Any> = emptyArray()) : UIText()

    fun toString(context: Context) =
        when (this) {
            is StringResource -> context.getString(id, *args)
        }
}
