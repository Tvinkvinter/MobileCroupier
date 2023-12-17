package com.atarusov.pokerapp.screens

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.atarusov.pokerapp.PlayersAdapter
import com.atarusov.pokerapp.R
import com.atarusov.pokerapp.databinding.FragmentConfigureScreenBinding
import com.atarusov.pokerapp.model.Player

class ConfigureScreenFragment : Fragment() {

    lateinit var binding: FragmentConfigureScreenBinding
    lateinit var adapter: PlayersAdapter

    private val viewModel: ConfigureScreenViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfigureScreenBinding.inflate(inflater)
        adapter = PlayersAdapter()

        binding.addPlayerBtn.setOnClickListener {
            viewModel.addPlayer(Player(0, Color.BLUE, null, getString(R.string.unknown_player), 0))
        }

        binding.startGameBtn.setOnClickListener{
            findNavController().navigate(R.id.action_configureScreenFragment_to_gameScreenFragment)
        }

        viewModel.players.observe(viewLifecycleOwner) {
            adapter.players = it
        }

        binding.playersList.adapter = adapter

        return binding.root
    }
}