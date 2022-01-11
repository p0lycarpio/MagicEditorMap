package com.polycarpio.magiceditormap

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import com.polycarpio.magiceditormap.models.GameList

class ListItem(context: Context) : FrameLayout(context) {
    init {
        inflate(context, R.layout.view_item, this)
    }
    fun populate(user: GameList?) {
        findViewById<TextView>(R.id.nomcarte).setText(user?.id)
    }
}