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

    private var pickedColors = mutableListOf<Int>()

    private val listener: PlayersListener = { it ->
        _players.value = it
    }

    init {
        loadPlayers()
        pickedColors = (_players.value as List<Player>).map { player -> player.color }.toMutableList()
    }

    override fun onCleared() {
        super.onCleared()
        playersService.removeListener(listener)
    }

    private fun loadPlayers() {
        playersService.addListener(listener)
    }

    fun addPlayer(player: Player) {
        if (playersService.getPlayerCount() < 8) {
            playersService.addPlayer(player)
            pickedColors.add(player.color)
        }
    }

    fun deletePlayer(player: Player) {
        playersService.deletePlayer(player)
        pickedColors.remove(player.color)
    }

    fun getPickedColors() = pickedColors
}