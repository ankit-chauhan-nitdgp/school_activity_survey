package com.hajmola.up.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.databinding.FragmentAdminHomeBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils.navigateWithBackStackClear
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding
    private val viewmodel : MyViewModel by viewModels()

    val spreadsheetId = AppConstants.SPREADSHEET_ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(layoutInflater, container, false)

        binding.registerUserButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_adminHome_to_RegisterPage)
        }

        binding.ScpMenuButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_adminHome_to_SchoolHomePage)
        }

        binding.dashBoardButton.setOnClickListener {
            findNavController().navigateWithSlideAnim(R.id.action_adminHome_to_FetchDataPage)
        }

        binding.logout.setOnClickListener {
            SessionManager.setLogin(requireContext(), false)
            SessionManager.setAdminLogin(requireContext(), false)
            viewmodel.updateScpDetailsData {
                viewmodel.getEmptyScpData()!!
            }
            viewmodel.updateSchoolDetailsData {
                viewmodel.getEmptySchoolData()!!
            }
            findNavController().navigateWithBackStackClear(
                R.id.action_adminHome_to_LoginPage,
                null,
                R.id.adminHomeFragment
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uploadToExcelButton.setOnClickListener {
            viewmodel.cleanAndUploadSchoolsToExcel(requireContext())
        }
//        viewmodel.fetchAndUploadSchools(requireContext())
        binding.openExcelSheetButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.SPREADSHEET_URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            requireContext().startActivity(intent)
        }


        // Collect StateFlows using standard lifecycle-aware coroutines
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collect School Response
                // Collect Excel Update Result
                launch {
                    viewmodel.excelUpdate.collect { state ->
                        when(state){
                            is UiState.Loading -> {
                                binding.uploadToExcelButton.text = "UPLOADING TO EXCEL"
                            }
                            is UiState.Success -> {
                                binding.uploadToExcelButton.text = "UPLOADED"
                                launch {
                                    delay(2000)
                                    viewmodel.resetExcelUploadStates()
                                }
                            }
                            is UiState.Error -> {
                                binding.uploadToExcelButton.text = "UPLOAD TO EXCEL"
                            }
                            is UiState.Idle -> { /* Do nothing */
                                binding.uploadToExcelButton.text = "Update Excel Sheet"}
                        }
                    }
                }

                // 3. New StateFlow Collector for getAllUserState
                launch {
                    viewmodel.getAllUserState.collect { state ->
                        when(state) {
                            is UiState.Loading -> {}
                            is UiState.Success -> {
                                state.data?.let { users ->
                                    viewmodel.updateAllUsers { users }
                                }
                            }
                            is UiState.Error -> {}
                            is UiState.Idle -> {}
                        }
                    }
                }
            }
        }

        // Keep your other flow/livedata logic below
        viewmodel.getAllUsers()
    }
}
