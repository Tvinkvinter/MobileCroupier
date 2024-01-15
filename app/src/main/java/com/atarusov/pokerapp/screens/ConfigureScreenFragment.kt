package com.atarusov.pokerapp.screens

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atarusov.pokerapp.PlayersAdapter
import com.atarusov.pokerapp.R
import com.atarusov.pokerapp.custom_views.ColorPickSquare
import com.atarusov.pokerapp.databinding.FragmentConfigureScreenBinding
import com.atarusov.pokerapp.model.Player

class ConfigureScreenFragment : Fragment() {

    private lateinit var binding: FragmentConfigureScreenBinding
    private lateinit var adapter: PlayersAdapter
    private lateinit var dialog: AlertDialog

    private val viewModel: ConfigureScreenViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfigureScreenBinding.inflate(inflater)
        adapter = PlayersAdapter()
        buildDialog()

        binding.addPlayerBtn.setOnClickListener {
            viewModel.showDialog(true)
        }

        binding.startGameBtn.setOnClickListener {
            findNavController().navigate(R.id.action_configureScreenFragment_to_gameScreenFragment)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.players = it.players
            if (it.maxPlayerCount) binding.addPlayerBtn.isEnabled = false
            when(it.message) {
                Message.EMPTY_NAME -> Toast.makeText(context, R.string.empty_name_error_message, Toast.LENGTH_SHORT).show()
                Message.UNPICKED_COLOR -> Toast.makeText(context, R.string.unpicked_color_error_message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
            if (it.dialogShown){
                showDialog()
            }
            else dialog.dismiss()
        }

        binding.playersList.adapter = adapter

        return binding.root
    }

    private fun buildDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialog)
        //TODO: get rid of the warning
        builder.setView(layoutInflater.inflate(R.layout.dialog_set_player, null))
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showDialog() {
        dialog.show()

        var pickedColor: Int? = null
        var username: String? = null

        dialog.findViewById<ImageView>(R.id.dialog_avatar).drawable.setTint(requireContext().getColor(R.color.white))
        dialog.findViewById<EditText>(R.id.dialog_username_edit_text).apply {
            clearFocus()
            text = null
            hint = getString(R.string.hint_edit_text)
        }

        dialog.findViewById<GridLayout>(R.id.colors_grid).forEach { colorSquare ->
            if ((colorSquare as ColorPickSquare).color in viewModel.uiState.value!!.pickedColors) {
                colorSquare.blocked = true
                colorSquare.picked = false
            } else {
                colorSquare.setOnClickListener { it ->
                    dialog.findViewById<GridLayout>(R.id.colors_grid).forEach {
                        (it as ColorPickSquare).picked = false
                    }
                    (it as ColorPickSquare).picked = true
                    pickedColor = it.color
                    dialog.findViewById<ImageView>(R.id.dialog_avatar).drawable.setTint(pickedColor!!)
                }
            }
        }

        dialog.findViewById<EditText>(R.id.dialog_username_edit_text).apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    username = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    if (count >= 8)
                        Toast.makeText(
                            context,
                            getString(R.string.max_length_error_message),
                            Toast.LENGTH_SHORT
                        ).show()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            setOnFocusChangeListener { view, _ -> (view as EditText).hint = null }
        }

        dialog.findViewById<Button>(R.id.dialog_cancel_button).setOnClickListener {
            viewModel.showDialog(false)
        }

        dialog.findViewById<Button>(R.id.dialog_apply_button).setOnClickListener {
            viewModel.addPlayer(Player(0, pickedColor, null, username, 0))
        }
    }
}