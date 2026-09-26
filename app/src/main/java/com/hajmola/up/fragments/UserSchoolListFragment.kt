package com.hajmola.up.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hajmola.up.MyViewModel
import com.hajmola.up.adapters.UserSchoolListAdapter
import com.hajmola.up.databinding.FragmentSchoolListBinding
import com.hajmola.up.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSchoolListFragment : Fragment() {

    private lateinit var binding: FragmentSchoolListBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: UserSchoolListAdapter

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
        adapter =
            UserSchoolListAdapter(requireContext(), viewmodel.getFetchedSchoolList())
        val rvLayoutManager = LinearLayoutManager(requireContext())
        rvLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSchoolData.layoutManager = rvLayoutManager

        binding.rvSchoolData.adapter = adapter
    }
}