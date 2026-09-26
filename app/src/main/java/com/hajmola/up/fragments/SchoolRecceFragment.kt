package com.hajmola.up.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hajmola.up.MyViewModel
import com.hajmola.up.data.ScpData
import com.hajmola.up.databinding.FragmentSchoolRecceBinding
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.Utils
import com.hajmola.up.utils.Utils.toDisplayString
import com.hajmola.up.views.MediaUploadView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolScpFragment : Fragment() {

    private lateinit var binding: FragmentSchoolRecceBinding
    private val viewmodel: MyViewModel by viewModels()

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var pdfLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var currentImageLoading: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Register gallery picker
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                requiredItem(currentImageLoading)?.setImagePreviewUri(it)
               updateUri(uri)
            }
        }

        // ✅ Register camera picker
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && imageUri != null) {
                    requiredItem(currentImageLoading)?.setImagePreviewUri(imageUri!!)
                    updateUri(imageUri!!)
                }
            }

        pdfLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                if (it != null) {
                    val thumbnail = generatePdfThumbnail(it, requireContext())
                    thumbnail?.let { bmp ->
                        requiredItem(currentImageLoading)?.setImageBitmap(bmp)
                    }

                    if (currentImageLoading == AppConstants.ACKNOWLEDGE_LETTER) {
                        viewmodel.updateScpDetailsData { scp ->
                            scp.copy(acknowledgementLetterUri = it)
                        }
                    } else if (currentImageLoading == AppConstants.PERMISSION_LETTER) {
                        viewmodel.updateScpDetailsData { scp ->
                            scp.copy(permissionLetterUri = it)
                        }
                    }
                }
            }
    }

    @SuppressLint("Recycle")
    private fun generatePdfThumbnail(uri: Uri, context: Context): Bitmap? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return null

            // PdfRenderer works with ParcelFileDescriptor
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // Open first page (index 0)
            val page = pdfRenderer.openPage(0)

            // Create bitmap for thumbnail
            val width = (page.width * 0.5).toInt()   // scale down if needed
            val height = (page.height * 0.5).toInt()
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Render page to bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Close resources
            page.close()
            pdfRenderer.close()

            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolRecceBinding.inflate(layoutInflater, container, false)

        binding.schoolScpTopBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldPermissionReceived.permissionLetterPhoto.setOnUploadClickListener {
            currentImageLoading = AppConstants.PERMISSION_LETTER
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.acknowledgementPhoto.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACKNOWLEDGE_LETTER
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity1Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_1_PHOTO
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity2Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_2_PHOTO
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity3Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_3_PHOTO
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity4Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_4_PHOTO
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity5Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_5_PHOTO
            showImagePickerDialog()
        }

        binding.fieldPermissionReceived.activity6Photo.setOnUploadClickListener {
            currentImageLoading = AppConstants.ACTIVITY_6_PHOTO
            showImagePickerDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scpData = viewmodel.getScpDetailsData()
        if (scpData != null) {
            setInputFields(scpData)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,

            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.fieldPermissionReceived.otherType.isChecked) {
                        viewmodel.updateScpDetailsData {
                            it.copy(
                                activityArea = binding.fieldPermissionReceived.otherTypeET.text.toString().trim(),
                              )
                        }
                    }

                    viewmodel.updateScpDetailsData {
                        it.copy(
                            unitsDistributed = binding.fieldPermissionReceived.unitsDistributed.getInput().trim(),
                            estimatedSessions = binding.fieldPermissionReceived.fieldsActivityDates.estimatedSessions.getInput().trim()
                        )
                    }

                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateActivityDates()
        }
        updatePermissionReceivedToggle()
        updateActivityAreaToggle()
    }


    private fun updatePermissionReceivedToggle() {
        binding.fieldPermissionReceived.yesReceived.setOnClickListener {
            binding.fieldPermissionReceived.noReceived.isChecked = false
            viewmodel.updateScpDetailsData { it.copy(permissionGiven = "Yes") }
        }

        binding.fieldPermissionReceived.noReceived.setOnClickListener {
            binding.fieldPermissionReceived.yesReceived.isChecked = false
            viewmodel.updateScpDetailsData { it.copy(permissionGiven = "No") }
        }
    }

    private fun updateActivityAreaToggle() {
        binding.fieldPermissionReceived.classroomType.setOnClickListener {
            binding.fieldPermissionReceived.auditoriumType.isChecked = false
            binding.fieldPermissionReceived.otherType.isChecked = false
            binding.fieldPermissionReceived.otherTypeET.isEnabled = false
            binding.fieldPermissionReceived.classroomType.isChecked = true
            viewmodel.updateScpDetailsData { it.copy(activityArea = "Classroom") }
        }

        binding.fieldPermissionReceived.auditoriumType.setOnClickListener {
            binding.fieldPermissionReceived.classroomType.isChecked = false
            binding.fieldPermissionReceived.auditoriumType.isChecked = true
            binding.fieldPermissionReceived.otherType.isChecked = false
            binding.fieldPermissionReceived.otherTypeET.isEnabled = false
            viewmodel.updateScpDetailsData { it.copy(activityArea = "Auditorium") }
        }

        binding.fieldPermissionReceived.otherType.setOnClickListener {
            binding.fieldPermissionReceived.classroomType.isChecked = false
            binding.fieldPermissionReceived.auditoriumType.isChecked = false
            binding.fieldPermissionReceived.otherType.isChecked = true
            binding.fieldPermissionReceived.otherTypeET.isEnabled = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateActivityDates() {
        binding.fieldPermissionReceived.fieldsActivityDates.activityDate.setOnClickListener {it as AppCompatEditText
            Utils.showDatePicker(requireContext()){ date->
                it.setText(date.toDisplayString())
                viewmodel.updateScpDetailsData {scpData ->
                    scpData.copy(activityDate = date.toDisplayString())
                }
            }
        }

        binding.fieldPermissionReceived.fieldsActivityDates.activityStartTime.setOnClickListener {it as AppCompatEditText
            Utils.showTimePicker(requireContext()){ time->
                it.setText(time.toDisplayString())
                viewmodel.updateScpDetailsData {scpData ->
                    scpData.copy(activityStartTime = time.toDisplayString())
                }
            }
        }

        binding.fieldPermissionReceived.fieldsActivityDates.activityEndTime.setOnClickListener {it as AppCompatEditText
            Utils.showTimePicker(requireContext()){ time->
                it.setText(time.toDisplayString())
                viewmodel.updateScpDetailsData {scpData ->
                    scpData.copy(activityEndTime = time.toDisplayString())
                }
            }
        }
    }

    private fun showImagePickerDialog() {
        var options = arrayOf("Camera", "Gallery")
        if (currentImageLoading == AppConstants.PERMISSION_LETTER || currentImageLoading == AppConstants.ACKNOWLEDGE_LETTER) {
            options = arrayOf("Camera", "Gallery", "Pdf")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> openPdfPicker()
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

    private fun openPdfPicker() {
        pdfLauncher.launch("application/pdf")
    }

    private fun requiredItem(current: String): MediaUploadView? {
        var view: MediaUploadView? = null
        when (current) {
            AppConstants.ACTIVITY_1_PHOTO -> view = binding.fieldPermissionReceived.activity1Photo
            AppConstants.ACTIVITY_2_PHOTO -> view = binding.fieldPermissionReceived.activity2Photo
            AppConstants.ACTIVITY_3_PHOTO -> view = binding.fieldPermissionReceived.activity3Photo
            AppConstants.ACTIVITY_4_PHOTO -> view = binding.fieldPermissionReceived.activity4Photo
            AppConstants.ACTIVITY_5_PHOTO -> view = binding.fieldPermissionReceived.activity5Photo
            AppConstants.ACTIVITY_6_PHOTO -> view = binding.fieldPermissionReceived.activity6Photo
            AppConstants.ACKNOWLEDGE_LETTER -> view =
                binding.fieldPermissionReceived.acknowledgementPhoto

            AppConstants.PERMISSION_LETTER -> view =
                binding.fieldPermissionReceived.permissionLetterPhoto

            else -> null
        }

        return view
    }

    private fun updateUri(uri: Uri){
        if (currentImageLoading == AppConstants.ACTIVITY_1_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity1Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACTIVITY_2_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity2Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACTIVITY_3_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity3Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACTIVITY_4_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity4Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACTIVITY_5_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity5Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACTIVITY_6_PHOTO) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(activity6Uri = uri)
            }
        } else if (currentImageLoading == AppConstants.ACKNOWLEDGE_LETTER) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(acknowledgementLetterUri = uri)
            }
        } else if (currentImageLoading == AppConstants.PERMISSION_LETTER) {
            viewmodel.updateScpDetailsData { scp ->
                scp.copy(permissionLetterUri = uri)
            }
        }
    }

    private fun setInputFields(
        scpData: ScpData
    ) {
        if (scpData.permissionGiven == "Yes"){
            binding.fieldPermissionReceived.yesReceived.isChecked = true
            binding.fieldPermissionReceived.noReceived.isChecked = false
        }else{
            binding.fieldPermissionReceived.yesReceived.isChecked = false
            binding.fieldPermissionReceived.noReceived.isChecked = true
        }

        binding.fieldPermissionReceived.fieldsActivityDates.activityDate.setText(scpData.activityDate)
        binding.fieldPermissionReceived.fieldsActivityDates.estimatedSessions.setInput(scpData.estimatedSessions)
        binding.fieldPermissionReceived.fieldsActivityDates.activityStartTime.setText(scpData.activityStartTime)
        binding.fieldPermissionReceived.fieldsActivityDates.activityEndTime.setText(scpData.activityEndTime)
        binding.fieldPermissionReceived.unitsDistributed.setInput(scpData.unitsDistributed)


        when (scpData.activityArea) {
            "Classroom" -> {
                binding.fieldPermissionReceived.classroomType.isChecked = true
                binding.fieldPermissionReceived.auditoriumType.isChecked = false
                binding.fieldPermissionReceived.otherType.isChecked = false
                binding.fieldPermissionReceived.otherTypeET.isEnabled = false
            }
            "Auditorium" -> {
                binding.fieldPermissionReceived.classroomType.isChecked = false
                binding.fieldPermissionReceived.auditoriumType.isChecked = true
                binding.fieldPermissionReceived.otherType.isChecked = false
                binding.fieldPermissionReceived.otherTypeET.isEnabled = false
            }
            else -> {
                binding.fieldPermissionReceived.classroomType.isChecked = false
                binding.fieldPermissionReceived.auditoriumType.isChecked = false
                binding.fieldPermissionReceived.otherType.isChecked = true
                binding.fieldPermissionReceived.otherTypeET.isEnabled = true
                binding.fieldPermissionReceived.otherTypeET.setText(scpData.activityArea)
            }
        }

        scpData.activity1Uri?.let { binding.fieldPermissionReceived.activity1Photo.setImagePreviewUri(it) }
        scpData.activity2Uri?.let { binding.fieldPermissionReceived.activity2Photo.setImagePreviewUri(it) }
        scpData.activity3Uri?.let { binding.fieldPermissionReceived.activity3Photo.setImagePreviewUri(it) }
        scpData.activity4Uri?.let { binding.fieldPermissionReceived.activity4Photo.setImagePreviewUri(it) }
        scpData.activity5Uri?.let { binding.fieldPermissionReceived.activity5Photo.setImagePreviewUri(it) }
        scpData.activity6Uri?.let { binding.fieldPermissionReceived.activity6Photo.setImagePreviewUri(it) }

        scpData.acknowledgementLetterUri?.let { uri ->
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(uri) ?: ""
            if (mimeType == "application/pdf") {
                val thumbnail = generatePdfThumbnail(uri, requireContext())
                thumbnail?.let { bmp ->
                    binding.fieldPermissionReceived.acknowledgementPhoto.setImageBitmap(bmp)
                }
            } else if (mimeType.startsWith("image/")) {
                binding.fieldPermissionReceived.acknowledgementPhoto.setImagePreviewUri(uri)
            } else {
                Toast.makeText(requireContext(),"Media format unsupported", Toast.LENGTH_LONG).show()
            }
        }


        scpData.permissionLetterUri?.let { uri ->
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(uri) ?: ""
            if (mimeType == "application/pdf") {
                val thumbnail = generatePdfThumbnail(uri, requireContext())
                thumbnail?.let { bmp ->
                    binding.fieldPermissionReceived.permissionLetterPhoto.setImageBitmap(bmp)
                }
            } else if (mimeType.startsWith("image/")) {
                binding.fieldPermissionReceived.permissionLetterPhoto.setImagePreviewUri(uri)
            } else {
                Toast.makeText(requireContext(),"Media format unsupported", Toast.LENGTH_LONG).show()
            }
        }

    }

}