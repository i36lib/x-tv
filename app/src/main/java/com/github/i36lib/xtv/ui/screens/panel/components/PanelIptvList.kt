package com.github.i36lib.xtv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import com.github.i36lib.xtv.data.entities.EpgList
import com.github.i36lib.xtv.data.entities.EpgList.Companion.currentProgrammes
import com.github.i36lib.xtv.data.entities.Iptv
import com.github.i36lib.xtv.data.entities.IptvList
import com.github.i36lib.xtv.ui.rememberChildPadding
import com.github.i36lib.xtv.ui.theme.MyTVTheme
import kotlin.math.max

@Composable
fun PanelIptvList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvList: IptvList = IptvList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    listState: TvLazyListState = rememberTvLazyListState(
        max(0, iptvList.indexOfFirst { it.name == currentIptv.name })
    ),
) {
    val childPadding = rememberChildPadding()

    TvLazyRow(
        state = listState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = childPadding.start, end = childPadding.end),
    ) {
        items(iptvList) {
            PanelIptvItem(
                iptv = it,
                programmes = epgList.currentProgrammes(it),
                onIptvSelected = { onIptvSelected(it) },
            )
        }
    }
}

@Preview
@Composable
private fun PanelIptvListPreview() {
    MyTVTheme {
        PanelIptvList(
            iptvList = IptvList.EXAMPLE,
            currentIptv = Iptv.EXAMPLE,
        )
    }
}