package com.github.i36lib.xtv.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import com.github.i36lib.xtv.data.entities.EpgList
import com.github.i36lib.xtv.data.entities.EpgProgrammeList
import com.github.i36lib.xtv.data.entities.IptvGroupList
import com.github.i36lib.xtv.data.repositories.EpgRepository
import com.github.i36lib.xtv.data.repositories.IptvRepository
import com.github.i36lib.xtv.ui.utils.SP
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreeViewModel @Inject constructor(
    iptvRepository: IptvRepository,
    epgRepository: EpgRepository,
) : ViewModel() {
    var uiState = mutableStateOf<HomeScreenUiState>(HomeScreenUiState.Loading(""))

    init {
        viewModelScope.launch {
            flow { emit(iptvRepository.getIptvGroups()) }.retryWhen { _, attempt ->
                if (attempt >= SP.httpRetryCount) return@retryWhen false

                uiState.value =
                    HomeScreenUiState.Loading("获取远程直播源(${attempt + 1}/${SP.httpRetryCount})...")
                delay(SP.httpRetryInterval)
                true
            }.catch { uiState.value = HomeScreenUiState.Error(it.message) }.map {
                uiState.value = HomeScreenUiState.Ready(iptvGroupList = it)
                it
            }
                // 开始获取epg
                .flatMapLatest { iptvGroupList ->
                    val channels =
                        iptvGroupList.flatMap { it.iptvs }.map { iptv -> iptv.channelName }
                    flow { emit(epgRepository.getEpgs(channels)) }
                }.retry(SP.httpRetryCount) { delay(SP.httpRetryInterval); true }
                .catch { emit(EpgList()) }.map { epgList ->
                    // 移除过期节目
                    epgList.copy(value = epgList.map { epg ->
                        epg.copy(
                            programmes = EpgProgrammeList(
                                epg.programmes.filter { programme ->
                                    System.currentTimeMillis() < programme.endAt
                                },
                            )
                        )
                    })
                }.map { epgList ->
                    uiState.value =
                        (uiState.value as HomeScreenUiState.Ready).copy(epgList = epgList)
                }.collect()
        }
    }
}

sealed interface HomeScreenUiState {
    data class Loading(val message: String?) : HomeScreenUiState
    data class Error(val message: String?) : HomeScreenUiState
    data class Ready(
        val iptvGroupList: IptvGroupList = IptvGroupList(),
        val epgList: EpgList = EpgList(),
    ) : HomeScreenUiState
}