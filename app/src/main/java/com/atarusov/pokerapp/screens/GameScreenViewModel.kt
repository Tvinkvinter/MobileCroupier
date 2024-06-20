package com.atarusov.pokerapp.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atarusov.pokerapp.model.Player
import com.atarusov.pokerapp.model.PlayersListener
import com.atarusov.pokerapp.model.PlayersService

data class GameScreenUiState(
    val players: List<Player> = listOf(),
    val message: Message? = null,
    val pot: Int = 0,
    val indexOfPlayerToAct: Int = 0
)

class GameScreenViewModel(
    private val playersService: PlayersService
) : ViewModel() {

    private val _uiState = MutableLiveData(GameScreenUiState())
    val uiState: LiveData<GameScreenUiState> = _uiState

    private val listener: PlayersListener = { it ->
        _uiState.value = _uiState.value!!.copy(players = it)
    }

    init {
        loadPlayers()
    }

    private fun loadPlayers() {
        playersService.addListener(listener)
    }

    private fun getNextPlayerIndex(): Int {
        var nextPlayerIndex = _uiState.value!!.indexOfPlayerToAct + 1
        if (nextPlayerIndex >= _uiState.value!!.players.size) nextPlayerIndex = 0
        return nextPlayerIndex
    }

    fun checkAction() {
        _uiState.value = _uiState.value!!.copy(indexOfPlayerToAct = getNextPlayerIndex())
    }

}