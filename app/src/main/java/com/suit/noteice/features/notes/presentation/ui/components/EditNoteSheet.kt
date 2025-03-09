package com.suit.noteice.features.notes.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suit.noteice.R
import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.features.notes.presentation.ui.NoteInputData
import com.suit.noteice.ui.theme.NoteIceTheme
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteSheet(
    note: Note,
    lastModifiedAt: String?,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
    onValueChange: (NoteInputData) -> Unit
) {
    println("Showing note: $note")
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            if (lastModifiedAt != null) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.last_time_modified) + " $lastModifiedAt",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                        ))
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
            NoteEditTextField(
                data = NoteInputData.Title(note.title),
                onValueChange = onValueChange
            )
            NoteEditTextField(
                data = NoteInputData.Content(note.content),
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}

@Composable
fun NoteEditTextField(
    data: NoteInputData,
    colors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    ),
    onValueChange: (NoteInputData) -> Unit,
    modifier: Modifier = Modifier
) {
    val updatedColors = when (data) {
        is NoteInputData.Title -> colors
        is NoteInputData.Content -> colors.copy(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    }
    TextField(
        value = data.value,
        colors = updatedColors,
        placeholder = {
            Text(text = stringResource(
                id = when(data) {
                    is NoteInputData.Title -> R.string.note_title_placeholder
                    is NoteInputData.Content -> R.string.note_content_placeholder
                }))
        },
        onValueChange = {
            onValueChange(
                when (data) {
                    is NoteInputData.Title -> NoteInputData.Title(it)
                    is NoteInputData.Content -> NoteInputData.Content(it)
                }
            )
        },
        modifier = modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditNoteSheetPreview() {
    NoteIceTheme {
        Surface {
            EditNoteSheet(
                sheetState = rememberStandardBottomSheetState(),
                note = Note(id = 0, title = "", content = "",
                    createdAt = Instant.now(), lastModifiedAt = Instant.now()),
                onValueChange = {},
                onDismiss = {},
                lastModifiedAt = "Today, 12:45"
            )
        }
    }
}