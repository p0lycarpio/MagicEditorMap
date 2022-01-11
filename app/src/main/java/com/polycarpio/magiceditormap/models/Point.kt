package com.polycarpio.magiceditormap.models

data class Point(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: Enum<TypePoint>
)