package com.suit.noteice.setup.auth

import com.suit.noteice.setup.notes.NotesClientConstants
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.notes.data.TokenData
import io.mockk.coEvery
import io.mockk.mockk

fun mockTokensManager(
    tokenData: TokenData = NotesClientConstants.defaultTokenData
): TokensManager {
    return mockk<TokensManager> {
        coEvery { saveTokenData(any()) } returns Unit
        coEvery { getSavedTokenData() } returns tokenData
        coEvery { clearTokenData() } returns Unit
    }
}