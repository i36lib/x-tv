package com.github.i36lib.xtv.data.repositories

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.github.i36lib.xtv.data.entities.Iptv
import com.github.i36lib.xtv.data.entities.IptvGroup
import com.github.i36lib.xtv.data.entities.IptvGroupList
import com.github.i36lib.xtv.data.entities.IptvList
import com.github.i36lib.xtv.data.models.IptvResponseItem
import com.github.i36lib.xtv.ui.utils.SP
import java.io.File
import javax.inject.Singleton

@Singleton
class IptvRepositoryImpl(private val context: Context) : IptvRepository {
    override suspend fun getIptvGroups(): IptvGroupList {
        val now = System.currentTimeMillis()

        if (now - SP.iptvSourceCachedAt < SP.iptvSourceCacheTime) {
            val cache = getCache()
            if (cache.isNotBlank()) {
                Log.d(TAG, "使用缓存直播源")
                return parseSource(cache)
            }
        }

        val data = fetchSource()
        setCache(data)
        SP.iptvSourceCachedAt = now

        return parseSource(data)
    }

    private fun getSourceType(): SourceType {
        return if (SP.iptvSourceUrl.endsWith(".m3u")) SourceType.M3U else SourceType.TVBOX
    }

    private suspend fun fetchSource(): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "获取远程直播源: ${SP.iptvSourceUrl}")

        val client = OkHttpClient()
        val request = Request.Builder().url(SP.iptvSourceUrl).build()

        try {
            return@withContext with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程直播源: $code")
                }

                return@with body!!.string()
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取远程直播源失败", e)
            throw Exception("获取远程直播源失败，请检查网络连接", e.cause)
        }
    }

    private fun getCacheFile(): File {
        return if (getSourceType() == SourceType.M3U) File(context.cacheDir, "iptv.m3u")
        else File(context.cacheDir, "iptv-tvbox.txt")
    }

    private suspend fun getCache() = withContext(Dispatchers.IO) {
        if (getCacheFile().exists()) getCacheFile().readText() else ""
    }

    private suspend fun setCache(data: String) = withContext(Dispatchers.IO) {
        getCacheFile().writeText(data)
    }

    private fun parseSourceM3u(data: String): IptvGroupList {
        val lines = data.split("\n")
        val iptvList = mutableListOf<IptvResponseItem>()

        lines.forEachIndexed { index, line ->
            if (!line.startsWith("#EXTINF")) return@forEachIndexed

            val name = line.split(",").last()
            val channelName = Regex("tvg-name=\"(.+?)\"").find(line)?.groupValues?.get(1) ?: name
            val groupName = Regex("group-title=\"(.+?)\"").find(line)?.groupValues?.get(1) ?: "其他"

            if (SP.iptvSourceSimplify) {
                if (!name.lowercase().startsWith("cctv") && !name.lowercase()
                        .endsWith("卫视")
                ) return@forEachIndexed
            }

            iptvList.add(
                IptvResponseItem(
                    name = name,
                    channelName = channelName,
                    groupName = groupName,
                    url = lines[index + 1]
                )
            )
        }

        return IptvGroupList(iptvList.groupBy { it.groupName }.map { groupEntry ->
            IptvGroup(
                name = groupEntry.key,
                iptvs = IptvList(groupEntry.value.groupBy { it.name }.map { nameEntry ->
                    Iptv(
                        name = nameEntry.key,
                        channelName = nameEntry.value.first().channelName,
                        urlList = nameEntry.value.map { it.url },
                    )
                }),
            )
        }).also {
            Log.d(
                TAG,
                "解析m3u完成: ${it.size}个分组, ${it.flatMap { group -> group.iptvs }.size}个频道"
            )
        }
    }

    private fun parseSourceTvbox(data: String): IptvGroupList {
        val lines = data.split("\n")
        val iptvList = mutableListOf<IptvResponseItem>()

        var groupName: String? = null
        lines.forEach { line ->
            if (line.isBlank() || line.startsWith("#")) return@forEach

            if (line.endsWith("#genre#")) {
                groupName = line.split(",").first()
            } else {
                val res = line.replace("，", ",").split(",")
                if (res.size < 2) return@forEach

                iptvList.add(
                    IptvResponseItem(
                        name = res[0],
                        channelName = res[0],
                        url = res[1],
                        groupName = groupName ?: "其他",
                    )
                )
            }
        }

        return IptvGroupList(iptvList.groupBy { it.groupName }.map { groupEntry ->
            IptvGroup(
                name = groupEntry.key,
                iptvs = IptvList(groupEntry.value.groupBy { it.name }.map { nameEntry ->
                    Iptv(
                        name = nameEntry.key,
                        channelName = nameEntry.value.first().channelName,
                        urlList = nameEntry.value.map { it.url },
                    )
                }),
            )
        }).also {
            Log.d(
                TAG,
                "解析tvbox完成: ${it.size}个分组, ${it.flatMap { group -> group.iptvs }.size}个频道"
            )
        }
    }

    private fun parseSource(data: String): IptvGroupList {
        try {
            return if (getSourceType() == SourceType.M3U) {
                parseSourceM3u(data)
            } else {
                parseSourceTvbox(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析直播源失败，请检查直播源格式", e)
            throw Exception("解析直播源失败，请检查直播源格式", e.cause)
        }
    }

    companion object {
        const val TAG = "IptvRepositoryImpl"

        enum class SourceType {
            M3U, TVBOX,
        }
    }
}