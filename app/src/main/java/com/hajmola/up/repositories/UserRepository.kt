package com.hajmola.up.repositories

import com.hajmola.up.data.DeleteSchoolRequest
import com.hajmola.up.data.FileResponse
import com.hajmola.up.data.SchoolDataResponse
import com.hajmola.up.data.SchoolRequest
import com.hajmola.up.data.auth.LoginRequest
import com.hajmola.up.data.auth.RegisterRequest
import com.hajmola.up.data.auth.School
import com.hajmola.up.data.auth.UserData
import com.hajmola.up.networks.ApiResponse
import com.hajmola.up.networks.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(request: LoginRequest): ApiResponse<UserData> = apiService.login(request)

    suspend fun register(request: RegisterRequest): ApiResponse<UserData> = apiService.register(request)

    suspend fun getUsers(): ApiResponse<List<UserData>> = apiService.getUsers()
    suspend fun getCityList(): ApiResponse<List<String>> = apiService.getCityList()

    suspend fun addSchool(request: SchoolRequest): ApiResponse<Unit> = apiService.addSchool(request)

    suspend fun getSchoolByCity(city: String): ApiResponse<List<School>> = apiService.getSchoolsByCity(city)

    suspend fun uploadFile(file: MultipartBody.Part): ApiResponse<FileResponse> = apiService.uploadFile(file)

//    suspend fun uploadSchool(parts: List<MultipartBody.Part>): ApiResponse<Unit> = apiService.uploadSchool(parts)

    suspend fun uploadSchool(stringFields: Map<String, RequestBody>, files: List<MultipartBody.Part>): ApiResponse<Unit> = apiService.uploadSchool(stringFields, files)

    suspend fun deleteSchool(id: DeleteSchoolRequest): ApiResponse<Unit> = apiService.deleteSchool(id)
    suspend fun getSchools(
        city: String? = null,
        uploadedBy: String? = null,
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<List<SchoolDataResponse>> {
        return apiService.getSchools(city = city,uploadedBy= uploadedBy, date = date, startDate =  startDate, endDate =  endDate)
    }

}
