package com.github.i36lib.xtv.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import com.github.i36lib.xtv.data.entities.Iptv
import com.github.i36lib.xtv.ui.rememberChildPadding
import com.github.i36lib.xtv.ui.screens.panel.components.PanelChannelNo
import com.github.i36lib.xtv.ui.screens.panel.components.PanelIptvInfo
import com.github.i36lib.xtv.ui.theme.MyTVTheme


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvTempPanel(
    modifier: Modifier = Modifier,
    channelNo: Int = 0,
    currentIptv: Iptv = Iptv.EMPTY,
    playerError: Boolean = false,
    programmes: Pair<String?, String?> = Pair(null, null),
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        PanelChannelNo(
            channelNo = channelNo.toString().padStart(2, '0'),
            modifier = Modifier
                .padding(top = childPadding.top, end = childPadding.end)
                .align(Alignment.TopEnd),
        )

        Surface(
            colors = NonInteractiveSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .padding(start = childPadding.start, bottom = childPadding.bottom)
                .align(Alignment.BottomStart),
        ) {
            PanelIptvInfo(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .sizeIn(maxWidth = 500.dp),
                iptv = currentIptv,
                playerError = playerError,
                programmes = programmes,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun IptvTempPanelPreview() {
    MyTVTheme {
        IptvTempPanel(
            channelNo = 1,
            currentIptv = Iptv.EXAMPLE,
            playerError = true,
            programmes = Pair(
                "实况录像-2023/2024赛季中国男子篮球职业联赛季后赛12进8第五场",
                "实况录像-2023/2024赛季中国男子篮球职业联赛季后赛12进8第五场"
            )
        )
    }
}