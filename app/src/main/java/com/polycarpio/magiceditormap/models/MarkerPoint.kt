package com.polycarpio.magiceditormap.models

import java.util.*

data class MarkerPoint(
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val type: TypePoint?
)