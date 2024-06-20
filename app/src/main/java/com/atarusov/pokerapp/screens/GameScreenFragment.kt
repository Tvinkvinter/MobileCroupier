package com.atarusov.pokerapp.screens

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.atarusov.pokerapp.ItemType
import com.atarusov.pokerapp.PlayersAdapter
import com.atarusov.pokerapp.R
import com.atarusov.pokerapp.databinding.FragmentGameScreenBinding

class GameScreenFragment : Fragment() {
    private lateinit var binding: FragmentGameScreenBinding
    private lateinit var adapter: PlayersAdapter

    private val viewModel: GameScreenViewModel by viewModels { factory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScreenBinding.inflate(inflater)

        adapter = PlayersAdapter(itemType = ItemType.Icon)
        binding.playersList.adapter = adapter

        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.players = it.players
            val currentPlayer = it.players[it.indexOfPlayerToAct]
            with(binding) {

                playersList.post {
                    playersList.children.forEachIndexed { index, view ->
                        view.findViewById<ImageView>(R.id.frame).visibility =
                            if (index == it.indexOfPlayerToAct) View.VISIBLE else View.INVISIBLE
                    }
                }


                curPlayerNameTv.text = currentPlayer.name
                playerStackNumTv.text = currentPlayer.stack.toString()
                playerBetNumTv.text = currentPlayer.currentBet.toString()


                avatarStroke.drawable.colorFilter =
                    BlendModeColorFilter(currentPlayer.color!!, BlendMode.MULTIPLY)
                horizontalLine.drawable.colorFilter =
                    BlendModeColorFilter(currentPlayer.color, BlendMode.MULTIPLY)
                avatarIcon.drawable.colorFilter =
                    BlendModeColorFilter(currentPlayer.color, BlendMode.SRC_ATOP)
            }
        }

        binding.checkBtn.setOnClickListener { viewModel.checkAction() }

        return binding.root
    }
}