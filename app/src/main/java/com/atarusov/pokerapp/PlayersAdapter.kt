package com.atarusov.pokerapp

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.atarusov.pokerapp.databinding.PlayerIconBinding
import com.atarusov.pokerapp.databinding.PlayerTileBinding
import com.atarusov.pokerapp.model.Player


enum class ItemType {
    Tile, Icon
}

class OnClickListener(val clickListener: (player: Player) -> Unit) {
    fun onClick(player: Player) = clickListener(player)
}

class PlayersAdapter(
    private val itemType: ItemType,
    private val tileClickListener: OnClickListener? = null,
    private val crossClickListener: OnClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var players: List<Player> = emptyList()
        set(newValue) {
            field = newValue
            //TODO: исправить выделение
            notifyDataSetChanged()
        }

    var isInDeleteMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (itemType) {
            ItemType.Tile -> {
                val binding = PlayerTileBinding.inflate(inflater, parent, false)
                PlayersViewHolderTile(binding)
            }

            ItemType.Icon -> {
                val binding = PlayerIconBinding.inflate(inflater, parent, false)
                PlayersViewHolderIcon(binding)
            }
        }
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player = players[position]

        when (itemType) {
            ItemType.Tile -> {
                with((holder as PlayersViewHolderTile).binding) {
                    holder.itemView.setOnClickListener {
                        tileClickListener?.onClick(player)
                    }

                    holder.itemView.findViewById<ImageView>(R.id.deleting_cross)
                        .setOnClickListener {
                            crossClickListener?.onClick(player)
                        }

                    playerNameTv.text = player.name
                    if (player.color != null) {
                        //TODO: get rid of the warning
                        playerNameTv.compoundDrawableTintList = ColorStateList.valueOf(player.color)
                        playerCard.setStrokeColor(ColorStateList.valueOf(player.color))
                    }

                    if (isInDeleteMode) {
                        holder.itemView.isEnabled = false
                        deletingCross.visibility = View.VISIBLE
                    } else {
                        holder.itemView.isEnabled = true
                        deletingCross.visibility = View.GONE
                    }
                }
            }

            ItemType.Icon -> {
                with((holder as PlayersViewHolderIcon).binding) {
                    playerNameTv.text = player.name
                    playerStackTv.text = player.stack.toString()
                    if (player.color != null) {
                        //avatarRing.imageTintList = ColorStateList.valueOf(player.color)
                        avatarRing.drawable.colorFilter =
                            BlendModeColorFilter(player.color, BlendMode.MULTIPLY)
                        playerAvatar.drawable.setTint(player.color)
                    }
                }
            }
        }
    }

    class PlayersViewHolderTile(
        val binding: PlayerTileBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class PlayersViewHolderIcon(
        val binding: PlayerIconBinding
    ) : RecyclerView.ViewHolder(binding.root)
}