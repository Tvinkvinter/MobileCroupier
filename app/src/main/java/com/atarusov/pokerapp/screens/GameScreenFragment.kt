package com.atarusov.pokerapp.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.atarusov.pokerapp.OnClickListener
import com.atarusov.pokerapp.PlayersAdapter
import com.atarusov.pokerapp.databinding.FragmentGameScreenBinding

class GameScreenFragment : Fragment() {
    private lateinit var binding: FragmentGameScreenBinding
    private lateinit var adapter: PlayersAdapter

    private val viewModel: ConfigureScreenViewModel by viewModels { factory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScreenBinding.inflate(inflater)

        adapter = PlayersAdapter(OnClickListener {
            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
        })
        binding.playersList.adapter = adapter

        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.players = it.players
        }

        return binding.root
    }
}