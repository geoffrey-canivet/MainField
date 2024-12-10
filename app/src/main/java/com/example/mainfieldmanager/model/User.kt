package com.example.mainfieldmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var username: String,
    var email: String,
    var banque: Long,
    var password: String,
    val role: String,
    var avatar: String,
    var nbPlots: Int,
    var trophee: MutableList<Int>

)