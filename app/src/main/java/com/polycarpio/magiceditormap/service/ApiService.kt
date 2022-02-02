package com.polycarpio.magiceditormap.service

import com.polycarpio.magiceditormap.models.GameList
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("game-list")
    suspend fun getGameList(): Response<MutableList<String>>

}