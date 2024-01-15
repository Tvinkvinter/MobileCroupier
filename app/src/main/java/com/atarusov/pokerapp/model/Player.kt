package com.atarusov.pokerapp.model

data class Player(
    val id: Int,
    val color: Int?,
    val photo: String?,
    val name: String?,
    val stack: Int
)
