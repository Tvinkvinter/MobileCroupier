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
                viewModel.showDialog(it)
            },
            crossClickListener = OnClickListener {
                viewModel.deletePlayer(it)
            })
        binding.playersList.adapter = adapter

        binding.addPlayerBtn.setOnClickListener {
            viewModel.showDialog()
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
                Message.MAX_NAME_LENGTH -> showMessage(getString(R.string.max_length_error_message))
                null -> {}
            }
        }

        buildDialog()

        viewModel.dialogUiState.observe(viewLifecycleOwner) {
            if (it.isDialogShown) {
                if (!dialog.isShowing) showDialog()
                dialog.findViewById<ImageView>(R.id.dialog_avatar).drawable.setTint(
                    it.pickedColor ?: requireContext().getColor(R.color.white)
                )
                dialog.findViewById<GridLayout>(R.id.colors_grid).forEach { square ->
                    (square as ColorPickSquare).picked =
                        square.color == viewModel.dialogUiState.value!!.pickedColor
                }
            } else dialog.dismiss()
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

    private fun showDialog() {
        buildDialog()
        dialog.show()

        dialog.findViewById<EditText>(R.id.dialog_username_edit_text).apply {
            if (viewModel.dialogUiState.value?.username != null)
                setText(viewModel.dialogUiState.value?.username)
            setOnFocusChangeListener { view, _ -> (view as EditText).hint = null }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.setUsernameInDialog(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        dialog.findViewById<GridLayout>(R.id.colors_grid).forEach { colorSquare ->
            if ((colorSquare as ColorPickSquare).color in viewModel.uiState.value!!.pickedColors) {
                colorSquare.blocked = colorSquare.color != viewModel.dialogUiState.value?.pickedColor
            }
            if (!colorSquare.blocked) {
                colorSquare.setOnClickListener {
                    viewModel.setPickedColorInDialog((it as ColorPickSquare).color)
                }
            }
        }
        dialog.setOnDismissListener {
            viewModel.hideDialog()
        }
        dialog.findViewById<Button>(R.id.dialog_cancel_button).setOnClickListener {
            viewModel.hideDialog()
        }

        dialog.findViewById<Button>(R.id.dialog_apply_button).setOnClickListener {
            viewModel.setPlayerInDialog()
        }
    }
}