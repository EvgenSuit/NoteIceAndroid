package com.suit.noteice.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.suit.noteice.R
import com.suit.noteice.ui.theme.NoteIceTheme

sealed class AuthFieldType {
    data object Email: AuthFieldType()
    data object Password: AuthFieldType()
}

@Composable
fun AuthTextField(
    value: String,
    authFieldType: AuthFieldType,
    onValueChange: (String) -> Unit
) {
    var showPassword by rememberSaveable {
        mutableStateOf(false)
    }
    val maxFieldLength = integerResource(id = R.integer.max_auth_field_length)
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxFieldLength)) },
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Text(text = stringResource(id =
            when(authFieldType) {
                is AuthFieldType.Email -> R.string.email
                is AuthFieldType.Password -> R.string.password
            }))
        },
        visualTransformation = if ((authFieldType is AuthFieldType.Password &&
            showPassword) || authFieldType is AuthFieldType.Email) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            if (authFieldType is AuthFieldType.Password) {
                val icon = if (showPassword) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = icon, contentDescription = icon.name)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(authFieldType.toString()))
}

@Preview
@Composable
fun AuthTextFieldPreview() {
    NoteIceTheme {
        Surface {
            AuthTextField(value = "", authFieldType = AuthFieldType.Password) {

            }
        }
    }
}