package com.polycarpio.magiceditormap.service

import com.polycarpio.magiceditormap.models.GameList
import com.polycarpio.magiceditormap.models.Point
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("game-list")
    suspend fun getGameList(): Response<MutableList<String>>

    @GET("game/{id}")
    suspend fun getGameById(@Path("id") id : String): Response<MutableList<Point>>

    @POST("game/{id}")
    suspend fun postGameById(@Field("id") id: String): Response<String>

    @DELETE("game/{id}")
    suspend fun deleteGameById(@Field("id") id: String): Response<String>

}