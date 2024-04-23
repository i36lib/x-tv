package com.github.i36lib.xtv.data.entities

/**
 * 直播源分组
 */
data class IptvGroup(
    /**
     * 分组名称
     */
    val name: String,

    /**
     * 直播源列表
     */
    val iptvs: IptvList,
)