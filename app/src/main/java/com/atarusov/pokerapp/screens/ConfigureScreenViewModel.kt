package com.atarusov.pokerapp.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atarusov.pokerapp.model.Player
import com.atarusov.pokerapp.model.PlayersListener
import com.atarusov.pokerapp.model.PlayersService

class ConfigureScreenViewModel(
    private val playersService: PlayersService
): ViewModel() {
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private val listener: PlayersListener = {
        _players.value = it
    }

    init {
        loadPlayers()
    }

    override fun onCleared() {
        super.onCleared()
        playersService.removeListener(listener)
    }

    fun loadPlayers() {
        playersService.addListener(listener)
    }

    fun addPlayer(player: Player) {
        playersService.addPlayer(player)
    }

    fun deletePlayer(player: Player) {
        playersService.deletePlayer(player)
    }
}