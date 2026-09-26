package com.hajmola.up.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.adapters.SchoolDataListAdapter
import com.hajmola.up.data.DeleteSchoolRequest
import com.hajmola.up.databinding.FragmentSchoolListBinding
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.Utils
import com.hajmola.up.utils.Utils.navigateWithSlideAnim
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolListFragment : Fragment() {

    private lateinit var binding: FragmentSchoolListBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: SchoolDataListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolListBinding.inflate(layoutInflater, container, false)

        binding.dashboardTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFromFetch = arguments?.getBoolean("FROM_FETCH_PAGE")
        if (isFromFetch == true) {
            SessionManager.setFetchHandled(requireContext(), true)
        }

        adapter =
            SchoolDataListAdapter(requireContext(), viewmodel.getFetchedSchoolList(), onClick =
            {data->
                viewmodel.updateSelectedSchoolData { data }
                findNavController().navigateWithSlideAnim(R.id.action_schoolListFragment_to_showSchoolDataFragment)
            },
                onDelete = { id ->
                    Log.d(Utils.TAG,"school Id to delete: $id")
                    viewmodel.deleteSchool(DeleteSchoolRequest(id.toString()))
                }
            )


        val rvLayoutManager = LinearLayoutManager(requireContext())
        rvLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSchoolData.layoutManager = rvLayoutManager

        binding.rvSchoolData.adapter = adapter
    }
}