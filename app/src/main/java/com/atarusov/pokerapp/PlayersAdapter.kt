package com.atarusov.pokerapp

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.atarusov.pokerapp.databinding.PlayerTileBinding
import com.atarusov.pokerapp.model.Player

class OnClickListener(val clickListener: (player: Player) -> Unit) {
    fun onClick(player: Player) = clickListener(player)
}

class PlayersAdapter(
    private val tileClickListener: OnClickListener,
    private val crossClickListener: OnClickListener
) : RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>() {

    var players: List<Player> = emptyList()
        set(newValue) {
            field = newValue
            //TODO: исправить выделение
            notifyDataSetChanged()
        }

    var isInDeleteMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlayerTileBinding.inflate(inflater, parent, false)
        return PlayersViewHolder(binding)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        val player = players[position]

        holder.itemView.setOnClickListener {
            tileClickListener.onClick(player)
        }

        holder.itemView.findViewById<ImageView>(R.id.deleting_cross).setOnClickListener {
            crossClickListener.onClick(player)
        }

        with(holder.binding) {
            playerNameTv.text = player.name
            if (player.color != null) {
                //TODO: get rid of the warning
                playerNameTv.compoundDrawableTintList = ColorStateList.valueOf(player.color)
                playerCard.setStrokeColor(ColorStateList.valueOf(player.color))
            }
        }

        if (isInDeleteMode) {
            holder.itemView.isEnabled = false
            holder.itemView.findViewById<ImageView>(R.id.deleting_cross).visibility = View.VISIBLE
        } else {
            holder.itemView.isEnabled = true
            holder.itemView.findViewById<ImageView>(R.id.deleting_cross).visibility = View.GONE
        }

    }

    class PlayersViewHolder(
        val binding: PlayerTileBinding
    ) : RecyclerView.ViewHolder(binding.root)
}