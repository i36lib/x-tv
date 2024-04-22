package com.github.i36lib.xtv.ui.screens.panel.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.github.i36lib.xtv.data.entities.Iptv
import com.github.i36lib.xtv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvItem(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    onIptvSelected: () -> Unit = {},
    programmes: Pair<String?, String?> = Pair(null, null),
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Card(
        modifier = modifier
            .width(250.dp)
            .height(100.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isFocused = true
                        onIptvSelected()
                    },
                )
            },
        shape = CardDefaults.shape(RoundedCornerShape(20.dp)),
        scale = CardDefaults.scale(focusedScale = 2.1f),
        colors = CardDefaults.colors(
            containerColor = Color(28,69,86,128),
            contentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = { onIptvSelected() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Start)
                .padding(start = 20.dp, top = 10.dp, bottom = 15.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )

            Text(
                text = programmes.first ?: "正在播放：无",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun PanelIptvItemPreview() {
    MyTVTheme {
        PanelIptvItem(
            iptv = Iptv.EXAMPLE,
        )
    }
}