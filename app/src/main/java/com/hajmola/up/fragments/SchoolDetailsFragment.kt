package com.hajmola.up.fragments

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hajmola.up.MyViewModel
import com.hajmola.up.data.ApprovingAuthority
import com.hajmola.up.data.PrincipalInfo
import com.hajmola.up.data.SchoolDetailsData
import com.hajmola.up.data.SchoolInfo
import com.hajmola.up.databinding.FragmentSchoolDetailsBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.Utils
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.views.MediaUploadView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SchoolDetailsFragment : Fragment() {

    private lateinit var binding: FragmentSchoolDetailsBinding
    private val viewmodel: MyViewModel by activityViewModels()

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var currentImageLoading: String = ""
    private var schoolDetailsData: SchoolDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register gallery picker
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uri ->
                requiredItem(currentImageLoading)?.setImagePreviewUri(uri)
                if (currentImageLoading == AppConstants.SCHOOL_FRONT_PHOTO) {
                    viewmodel.updateSchoolDetailsData {
                        it.copy(school = it.school.copy(uriFront = uri))
                    }
                } else if (currentImageLoading == AppConstants.SCHOOL_BACK_PHOTO) {
                    viewmodel.updateSchoolDetailsData {
                        it.copy(school = it.school.copy(uriBack = uri))
                    }
                }
            }
        }

        // Register camera picker
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && imageUri != null) {
                    requiredItem(currentImageLoading)?.setImagePreviewUri(imageUri!!)
                    if (currentImageLoading == AppConstants.SCHOOL_FRONT_PHOTO) {
                        viewmodel.updateSchoolDetailsData {
                            it.copy(school = it.school.copy(uriFront = imageUri))
                        }
                    } else if (currentImageLoading == AppConstants.SCHOOL_BACK_PHOTO) {
                        viewmodel.updateSchoolDetailsData {
                            it.copy(school = it.school.copy(uriBack = imageUri))
                        }
                    }
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolDetailsBinding.inflate(layoutInflater, container, false)

        binding.schoolDetailsTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        schoolDetailsData = viewmodel.getSchoolDetailsData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preFilledInfo = viewmodel.getSchoolDetailsData()
        if (preFilledInfo != null) {
            setInputFields(preFilledInfo.school, preFilledInfo.principal, preFilledInfo.approvingAuth)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,

            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewmodel.updateSchoolDetailsData {
                        it.copy(
                            school = it.school.copy(
                                name = binding.schoolName.getInput().trim(),
                                addressLine1 = binding.fieldSchoolAddress.etLine1Address.text.toString()
                                    .trim(),
                                addressLine2 = binding.fieldSchoolAddress.etLine2Address.text.toString()
                                    .trim(),
                                state = binding.schoolState.getInput().trim(),
                                pinCode = binding.schoolPinCode.getInput().trim(),
                                emailId = binding.schoolEmail.getInput().trim(),
                                board = binding.schoolBoard.getInput().trim(),
                                upToClass = binding.uptoClass.getInput().trim(),
                                totalStrength = binding.strengthTotal.getInput().trim(),
                                strength8to12 = binding.strength8to12.getInput().trim(),
                                avgSectionPerClass = binding.avgSecPerClass.getInput().trim(),
                                avgTutionFees = binding.avgTutionFees.getInput().trim()
                            ),

                            principal = it.principal.copy(
                                name = binding.principalName.getInput().trim(),
                                email = binding.principalEmail.getInput().trim(),
                                contact = binding.principalContactNo.getInput().trim()
                            ),
                            approvingAuth = it.approvingAuth.copy(
                                name = binding.authorityName.getInput().trim(),
                                email = binding.authorityEmail.getInput().trim(),
                                contact = binding.authorityContactNo.getInput().trim()
                            )
                        )
                    }

                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        )

        val spinner: AppCompatSpinner = binding.selectCityTV
        val cityList = viewmodel.getFetchedCityList() ?: emptyList()
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cityList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(Color.BLACK)
                return v
            }
        }
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val city = parent.getItemAtPosition(position) as String
                viewmodel.updateSchoolDetailsData { it.copy(school = it.school.copy(city = city)) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val position = adapter.getPosition(preFilledInfo?.school?.city)
        if (position >= 0) {
            spinner.setSelection(position)
        }



        binding.schoolFrontPhoto.setOnUploadClickListener {
            currentImageLoading = AppConstants.SCHOOL_FRONT_PHOTO
            showImagePickerDialog()
        }

        binding.schoolBackPhoto.setOnUploadClickListener {
            currentImageLoading = AppConstants.SCHOOL_BACK_PHOTO
            showImagePickerDialog()
        }

    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        imageUri?.let { cameraLauncher.launch(it) }
    }

    private fun requiredItem(current: String): MediaUploadView? {
        var view: MediaUploadView? = null
        when (current) {
            AppConstants.SCHOOL_FRONT_PHOTO -> view = binding.schoolFrontPhoto
            AppConstants.SCHOOL_BACK_PHOTO -> view = binding.schoolBackPhoto
            else -> null
        }

        return view
    }

    private fun setInputFields(
        schoolInfo: SchoolInfo,
        principalInfo: PrincipalInfo,
        authority: ApprovingAuthority
    ) {
        binding.schoolName.setInput(schoolInfo.name)
        binding.fieldSchoolAddress.etLine1Address.setText(schoolInfo.addressLine1)
        binding.fieldSchoolAddress.etLine2Address.setText(schoolInfo.addressLine2)
        binding.schoolState.setInput(schoolInfo.state)
        binding.schoolPinCode.setInput(schoolInfo.pinCode)
        binding.schoolEmail.setInput(schoolInfo.emailId)
        binding.schoolBoard.setInput(schoolInfo.board)
        binding.uptoClass.setInput(schoolInfo.upToClass)
        binding.strengthTotal.setInput(schoolInfo.totalStrength)
        binding.strength8to12.setInput(schoolInfo.strength8to12)
        binding.avgSecPerClass.setInput(schoolInfo.avgSectionPerClass)
        binding.avgTutionFees.setInput(schoolInfo.avgTutionFees)


        binding.principalName.setInput(principalInfo.name)
        binding.principalEmail.setInput(principalInfo.email)
        binding.principalContactNo.setInput(principalInfo.contact)

        binding.authorityName.setInput(authority.name)
        binding.authorityEmail.setInput(authority.email)
        binding.authorityContactNo.setInput(authority.contact)

        schoolInfo.uriFront?.let { binding.schoolFrontPhoto.setImagePreviewUri(it) }
        schoolInfo.uriBack?.let { binding.schoolBackPhoto.setImagePreviewUri(it) }
    }
}


