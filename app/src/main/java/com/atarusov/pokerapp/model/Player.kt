package com.atarusov.pokerapp.model

import java.util.UUID

data class Player(
    val color: Int?,
    val photo: String?,
    val name: String?,
    val stack: Int,
    val id: UUID = UUID.randomUUID()
)
