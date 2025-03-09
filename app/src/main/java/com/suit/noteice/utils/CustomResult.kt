package com.suit.noteice.utils

sealed class CustomResult {
    data object None: CustomResult()
    data object InProgress: CustomResult()
    data object Success: CustomResult()
    data object Error: CustomResult()
}

fun CustomResult.isInProgress() = this is CustomResult.InProgress