package com.hajmola.up.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.hajmola.up.MyViewModel
import com.hajmola.up.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Utils {

    const val TAG = "HajmolaUP_Logs"

    fun NavController.navigateWithSlideAnim(resId: Int, args: Bundle? = null) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left_pop_enter)
            .setPopExitAnim(R.anim.slide_out_right_pop_exit)
            .build()
        navigate(resId, args, navOptions)
    }

    fun NavController.navigateWithBackStackClear(desId: Int, args: Bundle? = null, currId: Int){
          val navOptions = NavOptions.Builder()
                .setPopUpTo(currId, true) // clears login from backstack
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left_pop_enter)
                .setPopExitAnim(R.anim.slide_out_right_pop_exit)
                .build()

        navigate(desId, args, navOptions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
        val today = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue-1, LocalDate.now().dayOfMonth)
        val datePicker = DatePickerDialog(
            ContextThemeWrapper(context, R.style.CustomDatePicker),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            today.year,
            today.monthValue,
            today.dayOfMonth
        )
        datePicker.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun LocalDate.toDisplayString(pattern: String = "dd-MM-yyyy") : String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimePicker(context: Context, onTimeSelected: (LocalTime) -> Unit) {
        val now = LocalTime.now()
        val timePicker = TimePickerDialog(
            ContextThemeWrapper(context, R.style.CustomDatePicker), // 👈 You can define a custom style or use R.style.ThemeOverlay_AppCompat_Dialog
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                onTimeSelected(selectedTime)
            },
            now.hour,
            now.minute,
            false // true = 24-hour format, false = 12-hour format
        )
        timePicker.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun LocalTime.toDisplayString(pattern: String = "hh:mm a") : String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }


//    fun getMultipartFromUri(context: Context, uri: Uri, paramName: String = "file"): MultipartBody.Part {
//        val contentResolver = context.contentResolver
//        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
//
//        val inputStream = contentResolver.openInputStream(uri)!!
//        val fileBytes = inputStream.readBytes()
//        inputStream.close()
//
//        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
//        val fileName = "upload_${System.currentTimeMillis()}"
//        Log.d("ankit_logs", "uploadFile ${requestBody.contentType()} fn: $fileName")
//
//        return MultipartBody.Part.createFormData(paramName, fileName, requestBody)
//    }


    fun getMultipartFromUri(context: Context, uri: Uri, paramName: String = "file"): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        // Get file name from content resolver
        val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "upload_${System.currentTimeMillis()}"

        val inputStream = contentResolver.openInputStream(uri)!!
        val fileBytes = inputStream.readBytes()
        inputStream.close()

        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, fileName, requestBody)
    }

    fun createPartFromString(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())

    fun prepareFilePart(partName: String, filePath: String?): MultipartBody.Part? {
        if (filePath.isNullOrEmpty()) return null
        val file = File(filePath)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun getCityList(viewModel: MyViewModel) : List<String>{
        val cityList = mutableListOf<String>()

        Log.d(TAG,"cityList UtilsState ${viewModel.getCityListState.value}" )


        when(viewModel.getCityListState.value){
            is UiState.Error -> {}
            is UiState.Idle -> {}
            is UiState.Success -> {
                (viewModel.getCityListState.value as UiState.Success<List<String>>).data?.let { list ->
                    cityList.addAll(list)
                }
            }
            is UiState.Loading -> {}
        }

        Log.d(TAG,"cityList Utils ${cityList.size}" )

        return cityList.toList()
    }

}