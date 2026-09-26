package com.hajmola.up.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.data.auth.LoginRequest
import com.hajmola.up.databinding.FragmentLoginBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.navigateWithBackStackClear
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Safely collect the StateFlow within the Fragment lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginUserState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.loginEnterTV.text = "verifying..."
                        }
                        is UiState.Success -> {
                            // 1. Reset state IMMEDIATELY so it won't fire again on back navigation
                            viewModel.resetExcelUploadStates()

                            SessionManager.setLogin(requireContext(), true)
                            val name: String? = state.data?.name
                            Log.d(TAG, "name: $name")

                            viewModel.updateScpDetailsData { it.copy(uploadedBy = name.toString()) }
                            SessionManager.setUploader(requireContext(), name)

                            if (name == AppConstants.ADMIN_NAME) {
                                Log.d(TAG, "ADMIN LOGIN")
                                SessionManager.setAdminLogin(requireContext(), true)
                                findNavController().navigateWithBackStackClear(
                                    R.id.action_loginFragment_to_AdminHomePage,
                                    null,
                                    R.id.loginFragment
                                )
                            } else {
                                SessionManager.setAdminLogin(requireContext(), false)
                                findNavController().navigateWithBackStackClear(
                                    R.id.action_loginFragment_to_MainMenuPage,
                                    null,
                                    R.id.loginFragment
                                )
                            }
                        }
                        is UiState.Error -> {
                            binding.loginEnterTV.text = "Login"
                            binding.userNameET.text?.clear()
                            binding.passwordET.text?.clear()
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()

                            // Optional: Reset state to Idle after displaying the error
                            viewModel.resetExcelUploadStates()
                        }
                        is UiState.Idle -> {
                            // Neutral state, do nothing
                        }
                    }
                }
            }
        }

        binding.loginEnterTV.setOnClickListener {
            val username = binding.userNameET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(LoginRequest(username, password))
            }
        }
    }
}
