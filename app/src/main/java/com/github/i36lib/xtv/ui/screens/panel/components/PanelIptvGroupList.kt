package com.github.i36lib.xtv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.github.i36lib.xtv.data.entities.EpgList
import com.github.i36lib.xtv.data.entities.Iptv
import com.github.i36lib.xtv.data.entities.IptvGroupList
import com.github.i36lib.xtv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import com.github.i36lib.xtv.ui.rememberChildPadding
import com.github.i36lib.xtv.ui.theme.MyTVTheme
import kotlin.math.max

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvGroupList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
) {
    val childPadding = rememberChildPadding()
    val listState = rememberTvLazyListState(max(0, iptvGroupList.iptvGroupIdx(currentIptv)))

    TvLazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = childPadding.bottom),
    ) {
        items(iptvGroupList) {
            Text(
                text = it.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = childPadding.start),
            )
            Spacer(modifier = Modifier.height(6.dp))
            PanelIptvList(
                currentIptv = currentIptv,
                iptvList = it.iptvs,
                epgList = epgList,
                onIptvSelected = onIptvSelected,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvGroupListPreview() {
    MyTVTheme {
        Box(modifier = Modifier.height(150.dp)) {
            PanelIptvGroupList(
                iptvGroupList = IptvGroupList.EXAMPLE,
                currentIptv = Iptv.EXAMPLE,
            )
        }
    }
}