package com.suit.noteice.utils.ui.commonComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.suit.noteice.R
import com.suit.noteice.ui.theme.NoteIceTheme

@Composable
fun ConstrainedColumn(
    isScrollable: Boolean = true,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    val maxDefinedWidth = dimensionResource(id = R.dimen.max_width)
    BoxWithConstraints(
    ) {
        val maxWidth = this.maxWidth
        Column(
            modifier = Modifier
                .fillMaxSize()
                .let { if (isScrollable) it.verticalScroll(rememberScrollState()) else it },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = verticalArrangement
        ) {
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .let {
                        if (maxDefinedWidth < maxWidth) it.width(maxDefinedWidth)
                        else it.fillMaxWidth()
                    }
            ) {
                content()
            }
        }
    }
}

@Preview(device = "spec:id=reference_tablet,shape=Normal,width=1280,height=800,unit=dp,dpi=240")
@Composable
fun ConstrainedColumnPreview() {
    NoteIceTheme {
        Surface {
            ConstrainedColumn {
                Text(text = "Hi".repeat(250))
            }
        }
    }
}