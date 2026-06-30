package com.wc2026.launcher.schedule

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// ─────────────────────────────────────────────
//  Retrofit interface for football-data.org v4
//  Docs: https://www.football-data.org/documentation/quickstart
//
//  WC 2026 competition code: WC
// ─────────────────────────────────────────────

interface FootballDataApi {

    @GET("v4/competitions/WC/matches")
    suspend fun getWorldCupMatches(
        @Header("X-Auth-Token") apiKey: String,
        @Query("status") status: String? = null   // SCHEDULED, LIVE, FINISHED
    ): MatchesResponse

    /** Group standings — available once the group stage begins */
    @GET("v4/competitions/WC/standings")
    suspend fun getWorldCupStandings(
        @Header("X-Auth-Token") apiKey: String
    ): StandingsResponse

    companion object {
        private const val BASE_URL = "https://api.football-data.org/"

        fun create(): FootballDataApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // KotlinJsonAdapterFactory is needed so Moshi can deserialise all
            // Kotlin data classes (including StandingsResponse) via reflection.
            // moshi-kotlin is already on the classpath via libs.moshi.
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(FootballDataApi::class.java)
        }
    }
}
