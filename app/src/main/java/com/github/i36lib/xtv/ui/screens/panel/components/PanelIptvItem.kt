package com.github.i36lib.xtv.ui.screens.panel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
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
    val roundedShape = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .width(
                if (isFocused) {
                    310.dp
                } else {
                    250.dp
                }
            )
            .height(
                if (isFocused) {
                    124.dp
                } else {
                    100.dp
                }
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isFocused = true
                        onIptvSelected()
                    },
                )
            }
            .border(1.dp, Color(0xFFA8B6C2), roundedShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isFocused) {
                        listOf(Color(0xFF377C91), Color(0xFF1A4151))
                    } else {
                        listOf(Color(0x88286986), Color(0x88286986))
                    }
                ),
                shape = roundedShape,
            ),
        shape = CardDefaults.shape(roundedShape),
        colors = CardDefaults.colors(
            containerColor = Color(0x881C4556),
            focusedContainerColor = Color(0x00000000),
        ),
        border = CardDefaults.border(
            border = Border.None,
            focusedBorder = Border.None,
        ),
        onClick = { onIptvSelected() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Start)
                .let {
                    if (isFocused) {
                        it.padding(start = 26.dp, top = 10.dp, bottom = 20.dp)
                    } else {
                        it.padding(start = 19.dp, top = 10.dp, bottom = 15.dp)
                    }
                },
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                color = Color(0xFFFFFFFF),
                fontSize = if (isFocused) {
                    33.sp
                } else {
                    28.sp
                },
                lineHeight = if (isFocused) {
                    46.sp
                } else {
                    40.sp
                },
                fontWeight = FontWeight.Bold
            )

            Text(
                text = programmes.first ?: "问苍茫第24集",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                fontSize = if (isFocused) {
                    26.sp
                } else {
                    24.sp
                },
                color = Color(0x88FFFFFF),
                lineHeight = if (isFocused) {
                    37.sp
                } else {
                    33.sp
                },
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