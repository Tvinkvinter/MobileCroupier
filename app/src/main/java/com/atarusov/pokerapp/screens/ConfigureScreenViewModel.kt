package com.atarusov.pokerapp.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atarusov.pokerapp.model.Player
import com.atarusov.pokerapp.model.PlayersListener
import com.atarusov.pokerapp.model.PlayersService
import java.util.UUID

enum class Message {
    EMPTY_NAME, UNPICKED_COLOR, MAX_NAME_LENGTH
}

data class ConfigureScreenUiState(
    val players: List<Player> = listOf(),
    val message: Message? = null,
    val isInDeleteMode: Boolean = false
)

val ConfigureScreenUiState.maxPlayersCountReached: Boolean
    get() = players.size >= 8
val ConfigureScreenUiState.pickedColors: List<Int>
    get() = players.map { player -> player.color!! }.toList()

data class SetPlayerDialogUiState(
    val isDialogShown: Boolean = false,
    val playerId: UUID? = null,
    val username: String? = null,
    val pickedColor: Int? = null
)

val SetPlayerDialogUiState.isDialogInUpdateMode: Boolean
    get() = playerId != null

class ConfigureScreenViewModel(
    private val playersService: PlayersService
) : ViewModel() {

    private val _uiState = MutableLiveData(ConfigureScreenUiState())
    val uiState: LiveData<ConfigureScreenUiState> = _uiState

    private val _dialogUiState = MutableLiveData(SetPlayerDialogUiState())
    val dialogUiState: LiveData<SetPlayerDialogUiState> = _dialogUiState

    private val listener: PlayersListener = { it ->
        _uiState.value = ConfigureScreenUiState(
            players = it,
            message = null,
            isInDeleteMode = _uiState.value?.isInDeleteMode ?: false
        )
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

    private fun addPlayer(player: Player) {
        if(checkName(player.name) && checkColor(player.color)) {
            playersService.addPlayer(player)
            hideDialog()
        }
    }

    private fun updatePlayer(player: Player) {
        if(checkName(player.name) && checkColor(player.color)) {
            playersService.updatePlayer(player)
            hideDialog()
        }
    }

    private fun checkName(username: String?): Boolean {
        return if (username == null) {
            showMessage(Message.EMPTY_NAME)
            false
        } else if (username.length  > 8){
            showMessage(Message.MAX_NAME_LENGTH)
            false
        } else true
    }

    private fun checkColor(color: Int?): Boolean {
        return if (color == null){
            showMessage(Message.UNPICKED_COLOR)
            false
        } else true
    }

    fun deletePlayer(player: Player) {
        playersService.deletePlayer(player)
    }

    fun swapPlayers(position1: Int, position2: Int) {
        playersService.swapPlayers(position1, position2)
    }

    fun switchDeleteMode() {
        _uiState.value = ConfigureScreenUiState(
            players = _uiState.value!!.players,
            isInDeleteMode = !_uiState.value!!.isInDeleteMode
        )
    }

    fun showDialog(player: Player? = null) {
        _dialogUiState.value = SetPlayerDialogUiState(
            isDialogShown = true,
            playerId = player?.id,
            username = player?.name,
            pickedColor = player?.color
        )
    }

    fun setPlayerInDialog(){
        if (_dialogUiState.value!!.isDialogInUpdateMode){
            updatePlayer(
                Player(
                    _dialogUiState.value!!.pickedColor, null,
                    _dialogUiState.value!!.username, 0,
                    _dialogUiState.value!!.playerId!!
                )
            )
        } else {
            addPlayer(Player(
                _dialogUiState.value!!.pickedColor, null,
                _dialogUiState.value!!.username, 0
            ))
        }
    }

    fun hideDialog() {
        _dialogUiState.value = SetPlayerDialogUiState(
            isDialogShown = false
        )
    }

    fun setUsernameInDialog(username: String) {
        _dialogUiState.value = SetPlayerDialogUiState(
            isDialogShown = _dialogUiState.value!!.isDialogShown,
            playerId = _dialogUiState.value!!.playerId,
            username = username,
            pickedColor = _dialogUiState.value!!.pickedColor
        )
    }

    fun setPickedColorInDialog(pickedColor: Int) {
        _dialogUiState.value = SetPlayerDialogUiState(
            isDialogShown = _dialogUiState.value!!.isDialogShown,
            playerId = _dialogUiState.value!!.playerId,
            username = _dialogUiState.value!!.username,
            pickedColor = pickedColor
        )
    }

    private fun showMessage(type: Message) {
        _uiState.value = ConfigureScreenUiState(
            players = _uiState.value!!.players,
            message = type,
            isInDeleteMode = _uiState.value!!.isInDeleteMode
        )
    }

    fun messageShown() {
        _uiState.value = ConfigureScreenUiState(
            players = _uiState.value!!.players,
            message = null,
            isInDeleteMode = _uiState.value!!.isInDeleteMode
        )
    }

}