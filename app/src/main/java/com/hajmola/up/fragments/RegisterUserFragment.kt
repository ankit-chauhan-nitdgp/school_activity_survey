package com.hajmola.up.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hajmola.up.MyViewModel
import com.hajmola.up.data.RegistrationRequest
import com.hajmola.up.data.auth.RegisterRequest
import com.hajmola.up.databinding.FragmentRegisterUserBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterUserFragment : Fragment() {

    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel : MyViewModel by viewModels()
    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterUserBinding.inflate(layoutInflater, container, false)

        binding.registerTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Collect StateFlow safely within the lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerUserState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.registerEnterTV.text = "registering..."
                        }

                        is UiState.Success -> {
                            // Reset state immediately so it won't repeat if fragment reconstructs
                            viewModel.resetRegisterState()

                            binding.registerEnterTV.text = "Register"
                            binding.nameET.text?.clear()
                            binding.userNameET.isFocusable = false
                            binding.nameET.isFocusable = false
                            binding.passwordET.isFocusable = false
                            binding.userNameET.text?.clear()
                            binding.passwordET.text?.clear()
                            Toast.makeText(requireContext(), "User registered: ${state.data?.name}", Toast.LENGTH_SHORT).show()
                        }

                        is UiState.Error -> {
                            // Reset error state to avoid re-triggering the error toast
                            viewModel.resetRegisterState()

                            binding.registerEnterTV.text = "Register"
                            binding.nameET.text?.clear()
                            binding.userNameET.text?.clear()
                            binding.passwordET.text?.clear()
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }

                        is UiState.Idle -> {
                            // Neutral state, do nothing
                        }
                    }
                }
            }
        }

        binding.registerEnterTV.setOnClickListener {
            if (binding.userNameET.text.toString().isNotEmpty()
                && binding.passwordET.text.toString().isNotEmpty()
                && binding.nameET.text.toString().isNotEmpty()
            ) {
                viewModel.registerUser(
                    RegisterRequest(
                        binding.nameET.text.toString().trim(),
                        binding.userNameET.text.toString().trim(),
                        binding.passwordET.text.toString().trim()
                    )
                )
            }
        }

        binding.userRole.setOnClickListener {
            if (!isAdmin){
                binding.nameET.setText(AppConstants.ADMIN_NAME)
                binding.nameET.isEnabled = false
                binding.userRole.text = "Admin"
                binding.registerEnterTV.text = "Register As Admin"
                isAdmin = true
            }else{
                binding.nameET.setText("")
                binding.nameET.isEnabled = true
                binding.userRole.text = "User"
                binding.registerEnterTV.text = "Register As User"
                isAdmin = false
            }
        }
    }
}
