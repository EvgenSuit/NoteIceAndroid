package com.suit.noteice.utils.notes

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import com.suit.noteice.utils.notes.data.TokenData
import com.suit.noteice.utils.notes.data.TokenDataSerializer
import kotlinx.coroutines.flow.first

val Context.tokensDataStore by dataStore(
    fileName = "tokens",
    serializer = TokenDataSerializer
)
class TokensManager(
    private val tokensDataStore: DataStore<TokenData>
) {
    suspend fun saveTokenData(data: TokenData) {
        tokensDataStore.updateData {
            data
        }
    }
    suspend fun getSavedTokenData(): TokenData? {
        return try {
            tokensDataStore.data.first()
        } catch (e: IOException) {
            null
        }
    }
}