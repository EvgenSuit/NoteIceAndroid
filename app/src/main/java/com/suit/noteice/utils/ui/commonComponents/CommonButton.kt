package com.suit.noteice.utils.ui.commonComponents

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CommonButton(
    @StringRes textRes: Int,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors()
) {
    ElevatedButton(
        onClick = onClick,
        colors = colors,
        modifier = Modifier.defaultMinSize(minWidth = 200.dp, minHeight = 40.dp)) {
        Text(text = stringResource(id = textRes),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(5.dp))
    }
}