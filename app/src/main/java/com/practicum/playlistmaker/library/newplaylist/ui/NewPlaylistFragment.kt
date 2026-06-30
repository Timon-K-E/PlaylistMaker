package com.practicum.playlistmaker.library.newplaylist.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.utils.showCustomToast

class NewPlaylistFragment : Fragment(R.layout.fragment_new_playlist) {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var exitDialog: AlertDialog? = null

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateCoverUri(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNewPlaylistBinding.bind(view)

        setupClickListeners()
        setupTextWatchers()
        observeViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            }
        )
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            viewModel.onBackPressed()
        }

        binding.coverContainer.setOnClickListener {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            viewModel.onCreatePlaylistClicked(requireContext())
        }
    }

    private fun setupTextWatchers() {
        binding.titleLayout.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.updatePlaylistName(text?.toString() ?: "")
        }

        binding.descriptionLayout.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.updatePlaylistDescription(text?.toString() ?: "")
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.createButton.isEnabled = state.isCreateButtonEnabled

            state.coverUri?.let { uri ->
                showCoverImage(uri)
            }

            state.createdPlaylistName?.let { name ->
                val message = getString(R.string.playlist_created, name)
                showCustomToast(message)
                viewModel.onToastShown()
            }

        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event?.let {
                when (it) {
                    NewPlaylistViewModel.NavigationEvent.NavigateBack -> {
                        findNavController().navigateUp()
                        viewModel.onNavigationHandled()
                    }
                    NewPlaylistViewModel.NavigationEvent.ShowExitDialog -> {
                        showExitDialog()
                        viewModel.onNavigationHandled()
                    }
                    NewPlaylistViewModel.NavigationEvent.DismissDialog -> {
                        exitDialog?.dismiss()
                        viewModel.onNavigationHandled()
                    }
                }
            }
        }
    }

    private fun showCoverImage(uri: Uri) {
        binding.albumCover.scaleType = ImageView.ScaleType.CENTER_CROP

        val radius = resources.getDimensionPixelSize(R.dimen.radius_size_8)

        Glide.with(this)
            .load(uri)
            .transform(
                com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                RoundedCorners(radius)
            )
            .into(binding.albumCover)
    }

    private fun showExitDialog() {
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_exit_playlist,
            null
        )

        exitDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnFinish).setOnClickListener {
            viewModel.onExitDialogConfirmed()
            exitDialog?.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            viewModel.onExitDialogCancelled()
            exitDialog?.dismiss()
        }

        exitDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        exitDialog?.show()
    }

    override fun onDestroyView() {
        exitDialog?.dismiss()
        exitDialog = null
        super.onDestroyView()
        _binding = null
    }
}