package com.polycarpio.magiceditormap.models

enum class TypePoint {
    TARGET,
    CHECKPOINT,
    ATTACK,
    BONUS
}

data class Point(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: Enum<TypePoint>
)
