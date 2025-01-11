package com.suit.noteice.utils.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.core.app.ApplicationProvider

fun ComposeContentTestRule.getString(
    @StringRes id: Int,
    vararg args: Any
) = ApplicationProvider.getApplicationContext<Context>().getString(id, *args)