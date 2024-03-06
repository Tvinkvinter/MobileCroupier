package com.atarusov.pokerapp.screens

import android.app.AlertDialog
import android.content.res.ColorStateList
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.atarusov.pokerapp.OnClickListener
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

        binding.trashCanIc.setOnClickListener {
            viewModel.switchDeleteMode()
        }

        adapter = PlayersAdapter(
            tileClickListener = OnClickListener {
                showDialog(it)
            },
            crossClickListener = OnClickListener {
                viewModel.deletePlayer(it)
            })
        binding.playersList.adapter = adapter

        binding.addPlayerBtn.setOnClickListener {
            showDialog()
        }

        binding.startGameBtn.setOnClickListener {
            findNavController().navigate(R.id.action_configureScreenFragment_to_gameScreenFragment)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.players = it.players

            if (it.isInDeleteMode) {
                binding.trashCanIc.imageTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.red))
                adapter.isInDeleteMode = true
                binding.addPlayerBtn.isEnabled = false
                binding.startGameBtn.isEnabled = false
            } else {
                binding.trashCanIc.imageTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.white))
                binding.addPlayerBtn.isEnabled = true
                binding.startGameBtn.isEnabled = true
                adapter.isInDeleteMode = false
            }

            if (it.maxPlayersCountReached) binding.addPlayerBtn.isEnabled = false
            when (it.message) {
                Message.EMPTY_NAME -> showMessage(getString(R.string.empty_name_error_message))
                Message.UNPICKED_COLOR -> showMessage(getString(R.string.unpicked_color_error_message))
                null -> {}
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = source.adapterPosition
                val targetPosition = target.adapterPosition

                viewModel.swapPlayers(sourcePosition, targetPosition)
                adapter.notifyItemMoved(sourcePosition, targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        })

        itemTouchHelper.attachToRecyclerView(binding.playersList)

        return binding.root
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.messageShown()
    }

    private fun buildDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialog)
        //TODO: get rid of the warning
        builder.setView(layoutInflater.inflate(R.layout.dialog_set_player, null))
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showDialog(curPlayer: Player? = null) {
        buildDialog()
        dialog.show()

        var pickedColor: Int? = curPlayer?.color
        var username: String? = curPlayer?.name

        dialog.findViewById<ImageView>(R.id.dialog_avatar).drawable.setTint(
            pickedColor ?: requireContext().getColor(R.color.white)
        )

        dialog.findViewById<EditText>(R.id.dialog_username_edit_text).apply {
            setOnFocusChangeListener { view, _ -> (view as EditText).hint = null }
            setText(username)

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
                        showMessage(getString(R.string.max_length_error_message))
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        dialog.findViewById<GridLayout>(R.id.colors_grid).forEach { colorSquare ->
            if ((colorSquare as ColorPickSquare).color in viewModel.uiState.value!!.pickedColors) {
                if (pickedColor == colorSquare.color) {
                    colorSquare.blocked = false
                    colorSquare.picked = true
                } else {
                    colorSquare.blocked = true
                    colorSquare.picked = false
                }
            }
            if (!colorSquare.blocked) {
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

        dialog.findViewById<Button>(R.id.dialog_cancel_button).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.dialog_apply_button).setOnClickListener {
            if (curPlayer == null) {
                val isSuccessful = viewModel.addPlayer(Player(pickedColor, null, username, 0))
                if (isSuccessful) dialog.dismiss()
            } else {
                val isSuccessful =
                    viewModel.updatePlayer(Player(pickedColor, null, username, 0, curPlayer.id))
                if (isSuccessful) dialog.dismiss()
            }
        }
    }
}