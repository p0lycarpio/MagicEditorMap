package com.polycarpio.magiceditormap.service

import com.polycarpio.magiceditormap.models.GameList
import com.polycarpio.magiceditormap.models.Point
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("game-list")
    suspend fun getGameList(): Response<MutableList<String>>

    @GET("game/{id}")
    suspend fun getGameById(@Path("id") id : Int): Response<MutableList<Point>>
}