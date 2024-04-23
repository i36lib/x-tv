package com.github.i36lib.xtv.data.repositories

import android.content.Context
import android.util.Log
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import com.github.i36lib.xtv.data.entities.Epg
import com.github.i36lib.xtv.data.entities.EpgList
import com.github.i36lib.xtv.data.entities.EpgProgramme
import com.github.i36lib.xtv.data.entities.EpgProgrammeList
import com.github.i36lib.xtv.ui.utils.SP
import java.io.File
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Singleton

@Singleton
class EpgRepositoryImpl(private val context: Context) : EpgRepository {
    override suspend fun getEpgs(filteredChannels: List<String>): EpgList {
        if (!SP.epgEnable) {
            return EpgList()
        }

        val xml = getXml()
        val hashCode = filteredChannels.hashCode()

        if (SP.epgCachedHash == hashCode) {
            val cache = getCache()
            if (cache != null) {
                Log.d(TAG, "使用缓存epg")
                return cache
            }
        }

        val epgList = parseFromXml(xml, filteredChannels)
        setCache(epgList)
        SP.epgCachedHash = hashCode

        return epgList
    }

    private suspend fun fetchXml(): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "获取远程xml: ${SP.epgXmlUrl}")

        val client = OkHttpClient()
        val request = Request.Builder().url(SP.epgXmlUrl).build()

        try {
            return@withContext with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程xml: $code")
                }

                return@with body!!.string()
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取远程xml失败", e)
            throw Exception("获取远程EPG失败，请检查网络连接", e.cause)
        }
    }

    private fun getCacheXmlFile(): File {
        return File(context.cacheDir, "epg.xml")
    }

    private suspend fun getCacheXml() = withContext(Dispatchers.IO) {
        val file = getCacheXmlFile()
        if (file.exists()) file.readText() else ""
    }

    private suspend fun setCacheXml(xml: String) = withContext(Dispatchers.IO) {
        getCacheXmlFile().writeText(xml)
    }

    private suspend fun getXml(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (dateFormat.format(System.currentTimeMillis()) == dateFormat.format(SP.epgXmlCachedAt)) {
            val cache = getCacheXml()
            if (cache.isNotBlank()) {
                Log.d(TAG, "使用缓存xml")
                return cache
            }
        } else {
            if (Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) < SP.epgRefreshTimeThreshold
            ) {
                Log.d(TAG, "未到时间点，不刷新epg")
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            }
        }

        val xml = fetchXml()
        setCacheXml(xml)
        SP.epgXmlCachedAt = System.currentTimeMillis()
        SP.epgCachedHash = 0

        return xml
    }

    private suspend fun parseFromXml(
        xmlString: String,
        filteredChannels: List<String> = emptyList(),
    ) = withContext(Dispatchers.Default) {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xmlString))

        val epgMap = mutableMapOf<String, Epg>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "channel") {
                        val channelId = parser.getAttributeValue(null, "id")
                        parser.nextTag()
                        val channelName = parser.nextText()

                        if (filteredChannels.isEmpty() || filteredChannels.contains(channelName)) {
                            epgMap[channelId] = Epg(channelName, EpgProgrammeList())
                        }
                    } else if (parser.name == "programme") {
                        val channelId = parser.getAttributeValue(null, "channel")
                        val startTime = parser.getAttributeValue(null, "start")
                        val stopTime = parser.getAttributeValue(null, "stop")
                        parser.nextTag()
                        val title = parser.nextText()

                        fun parseTime(time: String): Long {
                            if (time.length < 14) return 0

                            return SimpleDateFormat("yyyyMMddHHmmss Z", Locale.getDefault()).parse(
                                time
                            )?.time ?: 0
                        }

                        if (epgMap.containsKey(channelId)) {
                            epgMap[channelId] = epgMap[channelId]!!.copy(
                                programmes = EpgProgrammeList(
                                    epgMap[channelId]!!.programmes + listOf(
                                        EpgProgramme(
                                            startAt = parseTime(startTime),
                                            endAt = parseTime(stopTime),
                                            title = title,
                                        )
                                    )
                                )
                            )
                        }

                    }
                }
            }
            eventType = parser.next()
        }

        Log.d(TAG, "解析epg完成，共${epgMap.size}个频道")
        return@withContext EpgList(epgMap.values.toList())
    }

    private fun getCacheFile(): File {
        return File(context.cacheDir, "epg.json")
    }

    private suspend fun getCache() = withContext(Dispatchers.IO) {
        val file = getCacheFile()
        if (file.exists()) {
            EpgList(Json.decodeFromString<List<Epg>>(file.readText()))
        } else {
            null
        }
    }

    private suspend fun setCache(epgList: EpgList) = withContext(Dispatchers.IO) {
        getCacheFile().writeText(Json.encodeToString(epgList.value))
    }

    companion object {
        const val TAG = "EpgRepositoryImpl"
    }
}