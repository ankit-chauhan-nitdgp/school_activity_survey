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
import com.hajmola.up.databinding.FragmentSchoolHomeBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.navigateWithBackStackClear
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SchoolHomeFragment : Fragment() {

    private lateinit var binding: FragmentSchoolHomeBinding
    private val viewModel: MyViewModel by viewModels()

    private var schoolName: String = "None"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schoolName = arguments?.getString(AppConstants.SCHOOL_NAME)!!
        Log.d(TAG, "school name $schoolName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSchoolHomeBinding.inflate(layoutInflater)

        binding.schoolDetailsButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_schoolHome_to_SchoolDetails)
        }

        binding.ScpCompleteButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_schoolHome_to_SchoolScp)
        }

        binding.syncButton.setOnClickListener {
            val schoolData = viewModel.getSchoolDetailsData()
            if (schoolData?.school?.name.equals("") || schoolData?.school?.city == "-- Select City") {
                Toast.makeText(requireContext(), "Fill School Name or City", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.uploadSchoolInfo(requireContext())
        }

        binding.closeButton.setOnClickListener {
//            findNavController().navigateWithBackStackClear(R.id.action_schoolHome_to_MainMenu, args = null, R.id.schoolHomeFragment)
                requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Collect StateFlow safely within the lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.syncButton.isEnabled = false
                            binding.syncButton.text = "UPLOADING... PLEASE WAIT"
                        }

                        is UiState.Success -> {

                            viewModel.updateScpDetailsData {
                                viewModel.getEmptyScpData()!!
                            }
                            viewModel.updateSchoolDetailsData {
                                viewModel.getEmptySchoolData()!!
                            }
                            binding.syncButton.text = "UPLOADED SUCCESSFULLY "
                            // Clear error state so it doesn't toast again if view recreates
                            launch {
                                delay(3000)
                                // Reset state immediately so back press won't fire this again
                                viewModel.resetUploadState()
                            }
                        }

                        is UiState.Error -> {
                            Log.d(TAG, "msg: ${state.message}")
                            binding.syncButton.text = "UPLOAD FAILED: TRY AGAIN"
                            binding.syncButton.isEnabled = true
                        }

                        is UiState.Idle -> {
                            // Neutral state, do nothing
                            binding.syncButton.text = "UPLOAD TO SERVER"
                            binding.syncButton.isEnabled = true
                        }
                    }
                }
            }
        }

        binding.schoolNameTV.text = schoolName
    }
}