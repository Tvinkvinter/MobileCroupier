package com.atarusov.pokerapp

import android.app.Application
import com.atarusov.pokerapp.model.PlayersService

class App: Application() {
    val playersService = PlayersService()
}