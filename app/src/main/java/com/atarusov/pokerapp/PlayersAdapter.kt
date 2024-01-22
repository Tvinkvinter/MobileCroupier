package com.atarusov.pokerapp

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atarusov.pokerapp.databinding.PlayerTileBinding
import com.atarusov.pokerapp.model.Player

class PlayersAdapter : RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>() {

    var players: List<Player> = emptyList()
        set(newValue) {
            field = newValue
            //TODO: исправить выделение
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlayerTileBinding.inflate(inflater, parent, false)
        return PlayersViewHolder(binding)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        val player = players[position]
        with(holder.binding) {
            playerNameTv.text = player.name
            if (player.color != null) {
                playerNameTv.compoundDrawableTintList = ColorStateList.valueOf(player.color)
                playerCard.setStrokeColor(ColorStateList.valueOf(player.color))

            }
        }
    }

    class PlayersViewHolder(
        val binding: PlayerTileBinding
    ) : RecyclerView.ViewHolder(binding.root)
}