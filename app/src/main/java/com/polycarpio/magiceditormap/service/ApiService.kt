package com.polycarpio.magiceditormap.service

import com.polycarpio.magiceditormap.models.MarkerPoint
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("game-list")
    suspend fun getGameList(): Response<MutableList<String>>

    @GET("game/{id}")
   suspend fun getGameById(@Path("id") id : String): Response<MutableList<MarkerPoint>>

    @POST("game/{id}")
    suspend fun postGameById(@Path("id")id: String, @Body body:MutableList<MarkerPoint>): Response<String>

    @DELETE("game/{id}")
    suspend fun deleteGameById(@Path("id") id: String): Response<String>

}