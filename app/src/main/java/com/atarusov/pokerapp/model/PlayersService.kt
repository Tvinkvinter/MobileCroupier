package com.atarusov.pokerapp.model

typealias PlayersListener = (players: List<Player>) -> Unit

class PlayersService {

    private val players = mutableListOf<Player>()

    private val listeners = mutableListOf<PlayersListener>()

    fun getPlayers(): List<Player> {
        return players
    }

    fun getPlayerCount(): Int = players.size

    fun addPlayer(player: Player) {
        val playerToAdd =
            Player(getPlayerCount(), player.color, player.photo, player.name, player.stack)
        players.add(playerToAdd)
        notifyChanges()
    }

    fun updatePlayer(updatedPlayer: Player) {
        // id equals index in the list
        players[updatedPlayer.id] = updatedPlayer
        notifyChanges()
    }

    fun deletePlayer(player: Player) {
        val indexToDelete = players.indexOfFirst { it.id == player.id }
        if (indexToDelete != -1) {
            players.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun addListener(listener: PlayersListener) {
        listeners.add(listener)
        listener.invoke(players)
    }

    fun removeListener(listener: PlayersListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(players) }
    }
}