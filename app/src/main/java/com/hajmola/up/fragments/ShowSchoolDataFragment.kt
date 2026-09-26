package com.hajmola.up.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import com.hajmola.up.databinding.FragmentShowSchoolDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowSchoolDataFragment : Fragment() {

    private lateinit var binding: FragmentShowSchoolDataBinding
    private val viewModel : MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowSchoolDataBinding.inflate(layoutInflater, container, false)

        binding.schoolDataTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val schoolData = viewModel.getSelectedSchoolData()
        if (schoolData != null) {
            binding.tvName.text = schoolData?.name
            binding.tvAddress.text = getString(R.string.address, schoolData.address)
            binding.tvCity.text = getString(R.string.city, schoolData.city)
            binding.tvState.text = getString(R.string.state, schoolData.state)
            binding.tvPincode.text = getString(R.string.pin_code, schoolData.pin_code)
            binding.tvEmail.text = getString(R.string.email, schoolData.email_id)
            binding.tvBoard.text = getString(R.string.board, schoolData.board)
            binding.tvUptoClass.text = getString(R.string.up_to_class, schoolData.up_to_class)
            binding.tvTotalStrength.text = "Total Strength: ${schoolData.total_strength}"
            binding.tvStrength8to12.text = "Strength 8 to 12: ${schoolData.strength_8to12}"
            binding.avgSection.text = "Avg. Sec. per class: ${schoolData.avg_section_per_class}"
            binding.avgFee.text = "Avg Fee: ${schoolData.avg_tution_fees}"
            binding.tvPrinciName.text = "Principal Name: ${schoolData.principal_name}"
            binding.tvPrinciContact.text = "Principal Contact: ${schoolData.principal_contact}"
            binding.tvPrinciEmail.text = "Principal Email: ${schoolData.principal_email}"
            binding.tvAuthName.text = "Authority Name: ${schoolData.authority_name}"
            binding.tvAuthContact.text = "Authority Contact: ${schoolData.authority_contact}"
            binding.tvAuthEmail.text = "Authority Email: ${schoolData.authority_email}"
            binding.tvPermissionGiven.text = "Permission Given: ${schoolData.permission_given}"
            binding.tvActDate.text = "Activity Date: ${schoolData.activity_date}"
            binding.tvEstSession.text = "Estimated Session: ${schoolData.estimated_sessions}"
            binding.tvActStartTime.text = "Activity Start Time: ${schoolData.activity_start_time}"
            binding.tvActEndTime.text = "Activity End Time: ${schoolData.activity_end_time}"
            binding.tvUnitDistributed.text = "Unit Distributed: ${schoolData.units_distributed}"
            binding.tvActArea.text = "Activity Area: ${schoolData.activity_area}"
            binding.tvUploadedBy.text = "Uploaded By: ${schoolData.uploaded_by}"
            binding.tvUploadedAt.text = "Uploaded At: ${schoolData.uploaded_at}"

            // 🔗 For URI fields -> show button clicks
            binding.btnSchoolFront.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.uri_front
                )
            }
            binding.btnSchoolBack.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.uri_back
                )
            }
            binding.btnActivity1.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity1_uri
                )
            }
            binding.btnActivity2.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity2_uri
                )
            }
            binding.btnActivity3.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity3_uri
                )
            }
            binding.btnActivity4.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity4_uri
                )
            }
            binding.btnActivity5.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity5_uri
                )
            }
            binding.btnActivity6.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.activity6_uri
                )
            }
            binding.btnAckLetter.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.acknowledgement_letter_uri
                )
            }
            binding.btnPermissionLetter.setOnClickListener {
                openLink(
                    binding.root.context,
                    schoolData?.permission_letter_uri
                )
            }
        }
    }

    fun openLink(context: Context, url: String?) {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show()
        }
    }



}