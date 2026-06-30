package com.wc2026.launcher.schedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Fetches a player photo URL from TheSportsDB (free tier, no key needed).
 *
 * Priority:
 *   1. strCutout — transparent-background PNG (player "floats" on card)
 *   2. strThumb  — regular headshot/action photo
 *   3. null      — no image found; fall back to flag emoji in UI
 *
 * Results are cached in memory for the app's lifetime so each player is
 * only fetched once per session.
 */
class PlayerImageRepo {

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<String, String?>()

    /**
     * Returns the best available image URL for [playerName], or null if unavailable.
     * The result is whether null or a valid URL — both are cached to avoid retry thrash.
     */
    suspend fun fetchUrl(playerName: String): String? {
        // Return cached entry (including explicit nulls) without hitting the network again
        if (cache.containsKey(playerName)) return cache[playerName]

        val result = runCatching {
            val encoded = URLEncoder.encode(playerName, "UTF-8")
            val url = "https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=$encoded"

            val response = withContext(Dispatchers.IO) {
                client.newCall(
                    Request.Builder()
                        .url(url)
                        .header("User-Agent", "WC2026Launcher/1.0")
                        .build()
                ).execute()
            }

            if (!response.isSuccessful) return@runCatching null

            val body = response.body?.string() ?: return@runCatching null
            val json = JSONObject(body)
            val players = json.optJSONArray("player") ?: return@runCatching null
            if (players.length() == 0) return@runCatching null

            val first = players.getJSONObject(0)

            // Prefer the transparent-background cutout; fall back to thumb
            val cutout = first.optString("strCutout").takeIf { it.isNotBlank() }
            val thumb  = first.optString("strThumb").takeIf { it.isNotBlank() }

            cutout ?: thumb
        }.getOrNull()

        cache[playerName] = result
        return result
    }
}
