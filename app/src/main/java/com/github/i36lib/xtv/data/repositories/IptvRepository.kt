package com.github.i36lib.xtv.data.repositories

import com.github.i36lib.xtv.data.entities.IptvGroupList

interface IptvRepository {
    suspend fun getIptvGroups(): IptvGroupList
}