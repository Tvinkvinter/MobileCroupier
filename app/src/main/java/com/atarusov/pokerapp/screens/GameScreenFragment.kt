package com.atarusov.pokerapp.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.atarusov.pokerapp.PlayersAdapter
import com.atarusov.pokerapp.databinding.FragmentGameScreenBinding

class GameScreenFragment : Fragment() {
    lateinit var binding: FragmentGameScreenBinding
    lateinit var adapter: PlayersAdapter

    private val viewModel: ConfigureScreenViewModel by viewModels { factory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScreenBinding.inflate(inflater)

        adapter = PlayersAdapter()
        binding.playersList.adapter = adapter

        viewModel.players.observe(viewLifecycleOwner, Observer {
            adapter.players = it
        })

        return binding.root
    }
}