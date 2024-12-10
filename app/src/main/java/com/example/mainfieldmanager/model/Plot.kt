package com.example.mainfieldmanager.model

import com.example.mainfieldmanager.R

data class Plot(
    val id: Int,
    var imageRes: Int = R.drawable.plot_empty2,
    var cropType: String? = null
)
