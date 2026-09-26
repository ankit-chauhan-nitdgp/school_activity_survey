package com.hajmola.up.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.databinding.FragmentSchoolDataFetchBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import com.hajmola.up.utils.Utils.toDisplayString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SchoolDataFetchFragment : Fragment() {

    private lateinit var binding: FragmentSchoolDataFetchBinding
    private val viewmodel: MyViewModel by activityViewModels()

    private var fetchCity: String? = null
    private var uploadedBy: String? = null
    private var selectedDate: String? = null
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolDataFetchBinding.inflate(layoutInflater, container, false)

        binding.fetchDataTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerCity: AppCompatSpinner = binding.selectCityTV

        val adapterCity =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewmodel.getFetchedCityList()!!)

        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = adapterCity
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val city = parent.getItemAtPosition(position) as String
                fetchCity = if(city == AppConstants.SELECT_CITY) null else city
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val spinnerUploadedBy: AppCompatSpinner = binding.selectUploadedByTV
        val array = mutableListOf("-- Select Uploader --")
        val userList = viewmodel.getAllUsersList()
        val userNames : List<String> = userList.map { it.name!! }
        for (user in userNames){
            array.add(user)
        }
        val adapterUploadedBy =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array.toTypedArray())
        adapterUploadedBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUploadedBy.adapter = adapterUploadedBy
        spinnerUploadedBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val user = parent.getItemAtPosition(position) as String
                uploadedBy = if(user == "-- Select Uploader --") null else user
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.datePickerTV.setOnClickListener {
            Utils.showDatePicker(requireContext()){
                binding.startDatePickerTV.text = "Start Date"
                binding.endDatePickerTV.text = "End Date"
                binding.datePickerTV.text = it.toDisplayString()
                selectedDate = it.toDisplayString()
                startDate = null
                endDate = null
            }
        }

        binding.startDatePickerTV.setOnClickListener {
            Utils.showDatePicker(requireContext()){
                binding.datePickerTV.text = "Select Date"
                binding.startDatePickerTV.text = it.toDisplayString()
                startDate = it.toDisplayString()
                selectedDate = null
            }
        }

        binding.endDatePickerTV.setOnClickListener {
            Utils.showDatePicker(requireContext()){
                binding.datePickerTV.text = "Select Date"
                binding.endDatePickerTV.text = it.toDisplayString()
                endDate = it.toDisplayString()
                selectedDate = null
            }
        }

        binding.fetchEnterTV.setOnClickListener {
            SessionManager.setFetchHandled(requireContext(), false)
            viewmodel.updateSelectedFilters { it.copy(city = fetchCity, uploadedBy = uploadedBy, date = selectedDate, startDate = startDate, endDate = endDate) }
            Log.d("ankit_logs", "city = $fetchCity, uploadedBy = $uploadedBy, date = $selectedDate, startDate = $startDate, endDate = $endDate")
            viewmodel.fetchSchools(city = fetchCity, uploadedBy = uploadedBy, date = selectedDate, startDate = startDate, endDate = endDate)
        }

        // Safely collect StateFlow changes within the lifecyle scope
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.schoolResponseData.collect { state ->
                    when(state){
                        is UiState.Loading -> {
                            binding.fetchEnterTV.text = "Loading..."
                        }
                        is UiState.Success -> {
                            if (state.data != null){
                                Log.d("ankit_logs", "school data fetch frag ${state.data}")
                                viewmodel.updateFetchedSchoolList { state.data }

                                if (!SessionManager.isFetchHandled(requireContext())){
                                    val bundle = bundleOf("FROM_FETCH_PAGE" to true)
                                    findNavController().navigateWithSlideAnim(R.id.action_fetchSchool_to_schoolListFragment, args = bundle)
                                }
                            }
                        }
                        is UiState.Error -> {
                            // Reset state so error details don't re-trigger toasts on configuration change

                            binding.fetchEnterTV.text = "Fetch Data"
                            binding.selectCityTV.invalidate()
                            binding.selectUploadedByTV.invalidate()
                            binding.startDatePickerTV.text = "Select Date"
                            binding.startDatePickerTV.text = "Start Date"
                            binding.endDatePickerTV.text = "End Date"
                            Log.d(TAG, "Error Fetch School Data : ${state.message}")
                            Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                        is UiState.Idle -> {
                            // Reset the text when state goes back to neutral idle
                            binding.fetchEnterTV.text = "Fetch Data"
                        }
                    }
                }
            }
        }
    }
}
