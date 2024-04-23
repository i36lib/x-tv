package com.github.i36lib.xtv.data.utils

/**
 * 常量
 */
object Constants {
    /**
     * IPTV源地址
     */
    const val IPTV_SOURCE_URL =
        "https://mirror.ghproxy.com/https://raw.githubusercontent.com/zhumeng11/IPTV/main/IPTV.m3u"

    /**
     * IPTV源缓存时间（毫秒）
     */
    const val IPTV_SOURCE_CACHE_TIME = 1000 * 60 * 60 * 24L // 24小时

    /**
     * 节目单XML地址
     */
    const val EPG_XML_URL = "https://live.fanmingming.com/e.xml"

    /**
     * 节目单刷新时间阈值（小时）
     */
    const val EPG_REFRESH_TIME_THRESHOLD = 6 // 不到6点不刷新

    /**
     * GitHub最新版本信息
     */
    const val GITHUB_RELEASE_LATEST_URL =
        "https://api.github.com/repos/yaoxieyoulei/mytv-android/releases/latest"

    /**
     * GitHub加速代理地址
     */
    const val GITHUB_PROXY = "https://mirror.ghproxy.com/"

    /**
     * HTTP请求重试次数
     */
    const val HTTP_RETRY_COUNT = 10L

    /**
     * HTTP请求重试间隔时间（毫秒）
     */
    const val HTTP_RETRY_INTERVAL = 3000L
}