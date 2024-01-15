package com.atarusov.pokerapp.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atarusov.pokerapp.model.Player
import com.atarusov.pokerapp.model.PlayersListener
import com.atarusov.pokerapp.model.PlayersService

enum class Message {
    EMPTY_NAME, UNPICKED_COLOR
}

data class ConfigureScreenUiState(
    val players: List<Player> = listOf(),
    val maxPlayerCount: Boolean = players.size >= 8,
    val dialogShown: Boolean = false,
    val message: Message? = null
)

val ConfigureScreenUiState.pickedColors: List<Int>
    get() = players.map { player -> player.color!! }.toList()

class ConfigureScreenViewModel(
    private val playersService: PlayersService
): ViewModel() {

    private val _uiState = MutableLiveData(ConfigureScreenUiState())
    val uiState: LiveData<ConfigureScreenUiState> = _uiState

    private val listener: PlayersListener = { it ->
        _uiState.value = ConfigureScreenUiState(it)
    }

    init {
        loadPlayers()
    }

    override fun onCleared() {
        super.onCleared()
        playersService.removeListener(listener)
    }

    private fun loadPlayers() {
        playersService.addListener(listener)
    }

    fun addPlayer(player: Player) {
        if (!checkName(player))
            _uiState.value = ConfigureScreenUiState(
                players = _uiState.value!!.players,
                message = Message.EMPTY_NAME
            )
        else if (!checkColor(player))
            _uiState.value = ConfigureScreenUiState(
                players = _uiState.value!!.players,
                message = Message.UNPICKED_COLOR
            )
        else{
            playersService.addPlayer(player)
            showDialog(false)
        }
    }

    fun checkName(player: Player): Boolean {
        return player.name != null
    }

    fun checkColor(player: Player): Boolean {
        return player.color != null
    }

    fun deletePlayer(player: Player) {
        playersService.deletePlayer(player)
    }

    fun showDialog(show: Boolean){
        _uiState.value = ConfigureScreenUiState(
            players = _uiState.value!!.players,
            dialogShown = show
        )
    }
}