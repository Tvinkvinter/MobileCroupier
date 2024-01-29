package com.atarusov.pokerapp.model

import java.util.Collections

typealias PlayersListener = (players: List<Player>) -> Unit

class PlayersService {

    private val players = mutableListOf<Player>()

    private val listeners = mutableListOf<PlayersListener>()

    fun getPlayers(): List<Player> {
        return players
    }

    fun getPlayerCount(): Int = players.size

    fun addPlayer(player: Player) {
        players.add(player)
        notifyChanges()
    }

    fun updatePlayer(updatedPlayer: Player) {
        players.forEachIndexed { index, player ->
            if (player.id == updatedPlayer.id){
                players[index] = updatedPlayer
                return@forEachIndexed
            }
        }
        notifyChanges()
    }

    fun deletePlayer(player: Player) {
        val indexToDelete = players.indexOfFirst { it.id == player.id }
        if (indexToDelete != -1) {
            players.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun swapPlayers(position1: Int, position2: Int){
        Collections.swap(players, position1, position2)
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