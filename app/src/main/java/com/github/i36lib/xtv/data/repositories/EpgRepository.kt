package com.github.i36lib.xtv.data.repositories


import com.github.i36lib.xtv.data.entities.EpgList

interface EpgRepository {
    suspend fun getEpgs(filteredChannels: List<String>): EpgList
}