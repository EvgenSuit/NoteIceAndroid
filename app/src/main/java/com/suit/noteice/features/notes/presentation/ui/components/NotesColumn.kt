package com.suit.noteice.features.notes.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suit.noteice.R
import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.ui.theme.NoteIceTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotesColumn(
    notes: List<Note>,
    enabled: Boolean,
    selectedNotes: List<Note>,
    onClick: (Long) -> Unit,
    onSelect: (Long) -> Unit
) {
    if (notes.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notes, key = { it.id }) { note ->
                val isSelected by remember(selectedNotes) {
                    mutableStateOf(selectedNotes.contains(note))
                }
                NoteComponent(
                    note = note,
                    enabled = enabled,
                    isSelected = isSelected,
                    isInSelectionMode = selectedNotes.isNotEmpty(),
                    onClick = { onClick(note.id) },
                    onSelect = { onSelect(note.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_notes),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun NoteComponent(
    note: Note,
    enabled: Boolean,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewConfiguration = LocalViewConfiguration.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val content = note.content
    LaunchedEffect(interactionSource, isInSelectionMode) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    // when selection mode is off, wait for it to become active and select the current note,
                    // otherwise just select the curr note
                    if (!isInSelectionMode) {
                        delay(viewConfiguration.longPressTimeoutMillis)
                        onSelect()
                    } else onSelect()
                }
                is PressInteraction.Release -> {
                    // when selection mode is off,
                    if (!isInSelectionMode) onClick()
                }
            }
        }
    }
    val containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val bottomShadowColor = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer
    Box(
        modifier = modifier.padding(5.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        ElevatedButton(
            onClick = {},
            enabled = enabled,
            shape = RoundedCornerShape(10.dp),
            interactionSource = interactionSource,
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 12.dp
            ),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = containerColor,
            ),
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)

            ) {
                if (content.isNotBlank()) {
                    Text(text = content,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 19.sp
                        )
                    )
                }
                    Text(text = note.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                    )
            }
        }
        Box(modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .height(50.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        bottomShadowColor.copy(0.4f)
                    ),
                )
            ))
    }
}

@Preview
@Composable
fun NoteComponentPreview() {
    NoteIceTheme {
        Surface {
            NoteComponent(
                note = Note(title = "Some title".repeat(4),
                    content = "Some content ".repeat(55)
                ),
                isSelected = false,
                isInSelectionMode = true,
                onClick = {},
                enabled = true,
                onSelect = {})
        }
    }
}

@Preview
@Composable
fun NoteColumnPreview() {
    val notes = listOf(
        Note(title = "Some title"),
        Note(id = 1, title = "Some title", content = "Some content"),
        Note(id = 2, title = "Some title"),
        Note(id = 3, title = "Some title"),
    )
    NoteIceTheme {
        Surface {
            NotesColumn(
                notes = notes,
                selectedNotes = emptyList(),
                onClick = {},
                enabled = true,
                onSelect = {})
        }
    }
}