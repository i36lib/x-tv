package com.github.i36lib.xtv.ui.screens.panel.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.github.i36lib.xtv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelChannelNo(
    modifier: Modifier = Modifier,
    channelNo: String = "",
) {
    Text(
        modifier = modifier,
        text = channelNo,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview
@Composable
private fun PanelChannelNoPreview() {
    MyTVTheme {
        PanelChannelNo(channelNo = "01")
    }
}