package com.hajmola.up.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.data.ExcelSyncState
import com.hajmola.up.databinding.FragmentMainMenuBinding
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.navigateWithBackStackClear
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private val viewModel: MyViewModel by viewModels()

    private var samplePhoto: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMenuBinding.inflate(layoutInflater, container, false)

        binding.ScpButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_mainMenu_to_SchoolHome)
        }

//        binding.syncButton.setOnClickListener {
//            val schoolData = viewModel.getSchoolDetailsData()
//            if (schoolData?.school?.name.equals("") || schoolData?.school?.city == "-- Select City") {
//                Toast.makeText(requireContext(), "Fill School Name or City", Toast.LENGTH_LONG)
//                    .show()
//                return@setOnClickListener
//            }
//            viewModel.uploadSchoolInfo(requireContext())
//        }

        binding.logout.setOnClickListener {
            SessionManager.setLogin(requireContext(), false)
            SessionManager.setAdminLogin(requireContext(), false)
            viewModel.updateScpDetailsData {
                viewModel.getEmptyScpData()!!
            }
            viewModel.updateSchoolDetailsData {
                viewModel.getEmptySchoolData()!!
            }
            findNavController().navigateWithBackStackClear(
                R.id.action_mainMenu_to_Login,
                null,
                R.id.mainMenuFragment
            )
        }

        binding.myUploads.setOnClickListener {

            SessionManager.setFetchHandled(requireContext(),false)
            viewModel.fetchSchools(uploadedBy = SessionManager.getUploader(requireContext())!!)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Collect StateFlow safely within the lifecycle
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uploadState.collect { state ->
//                    when (state) {
//                        is UiState.Loading -> {
//                            binding.syncStatus.text = "Sync Status: Uploading"
//                            binding.syncButton.isEnabled = false
//                        }
//
//                        is UiState.Success -> {
//
//                            viewModel.updateScpDetailsData {
//                                viewModel.getEmptyScpData()!!
//                            }
//                            viewModel.updateSchoolDetailsData {
//                                viewModel.getEmptySchoolData()!!
//                            }
//                            binding.syncStatus.text = "Sync Status: Uploaded Successfully"
//                            // Clear error state so it doesn't toast again if view recreates
//                            launch {
//                                delay(2000)
//                                // Reset state immediately so back press won't fire this again
//                                viewModel.resetUploadState()
//                            }
//                            binding.syncButton.isEnabled = true
//                        }
//
//                        is UiState.Error -> {
//                            Log.d(TAG, "msg: ${state.message}")
//                            binding.syncStatus.text = "Sync Status: Failed to upload"
//                            binding.syncButton.isEnabled = true
//                        }
//
//                        is UiState.Idle -> {
//                            // Neutral state, do nothing
//                            binding.syncStatus.text = "Sync Status: Idle"
//                            binding.syncButton.isEnabled = true
//                        }
//                    }
//                }
//            }
//        }

        val name = SessionManager.getUploader(requireContext())
        binding.greetings.text = "Hi, $name"
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        binding.dateTV.text = formattedDate


        // Safely collect StateFlow changes within the lifecycle scope
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.schoolResponseData.collect { state ->
                    when (state) {
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            if (state.data != null) {
                                viewModel.updateFetchedSchoolList { state.data }

                                if (!SessionManager.isFetchHandled(requireContext())) {
                                    val bundle = bundleOf()
                                    SessionManager.setFetchHandled(requireContext(), true)
                                    findNavController().navigateWithSlideAnim(
                                        R.id.action_mainMenu_to_UserSchoolList,
                                        args = bundle
                                    )
                                }
                            }
                        }

                        is UiState.Error -> {}
                        is UiState.Idle -> {
                        }
                    }
                }
            }
        }
    }
}
