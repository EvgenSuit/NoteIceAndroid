package com.suit.noteice.features.notes.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suit.noteice.ui.theme.NoteIceTheme
import com.suit.noteice.R
import com.suit.noteice.utils.ui.commonComponents.CommonButton

@Composable
fun LoadingScreen(
    notesFetchFailed: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
        if (notesFetchFailed) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 150.dp)
            ) {
                Text(text = stringResource(id = R.string.could_not_fetch_notes),
                    style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    val retryIcon = Icons.Filled.Refresh
                    Icon(imageVector = retryIcon, contentDescription = retryIcon.name)
                    CommonButton(
                        textRes = R.string.retry,
                        onClick = onRetry)
                }
            }
        }
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    NoteIceTheme {
        Surface {
            LoadingScreen(
                notesFetchFailed = true,
                onRetry = {})
        }
    }
}