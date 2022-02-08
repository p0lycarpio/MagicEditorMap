package com.polycarpio.magiceditormap.models

data class MarkerPoint(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: TypePoint
)