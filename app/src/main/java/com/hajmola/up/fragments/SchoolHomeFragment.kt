package com.hajmola.up.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.databinding.FragmentSchoolHomeBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.navigateWithBackStackClear
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import dagger.hilt.android.AndroidEntryPoint

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

        binding.closeButton.setOnClickListener {
//            findNavController().navigateWithBackStackClear(R.id.action_schoolHome_to_MainMenu, args = null, R.id.schoolHomeFragment)
                requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.schoolNameTV.text = schoolName
    }
}