package com.hajmola.up.networks

import com.hajmola.up.data.AuthRequest
import com.hajmola.up.data.DeleteSchoolRequest
import com.hajmola.up.data.FileResponse
import com.hajmola.up.data.RegistrationRequest
import com.hajmola.up.data.auth.School
import com.hajmola.up.data.SchoolDataResponse
import com.hajmola.up.data.SchoolRequest
import com.hajmola.up.data.auth.ForgotPasswordData
import com.hajmola.up.data.auth.ForgotPasswordRequest
import com.hajmola.up.data.auth.LoginRequest
import com.hajmola.up.data.auth.RegisterRequest
import com.hajmola.up.data.auth.ResetPasswordRequest
import com.hajmola.up.data.auth.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("school/add")
    suspend fun addSchool(@Body school: SchoolRequest): ApiResponse<Unit>

    @GET("school/list")
    suspend fun getSchool(): ApiResponse<List<SchoolDataResponse>>

    @GET("school/bycity/{city}")
    suspend fun getSchoolsByCity(@Path("city") city: String): ApiResponse<List<School>>

    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(@Part file: MultipartBody.Part): ApiResponse<FileResponse>

    @Multipart
    @POST("upload/file")
    suspend fun uploadSchool(@Part parts: List<MultipartBody.Part>): ApiResponse<Unit>

    @Multipart
    @POST("upload/save_school")
    suspend fun uploadSchool(
        @PartMap stringFields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part files: List<MultipartBody.Part>
    ): ApiResponse<Unit>

    @POST("upload/delete_school")
    suspend fun deleteSchool(@Body schoolId: DeleteSchoolRequest): ApiResponse<Unit>

    @GET("upload/get_schools")
    suspend fun getSchools(
        @Query("city") city: String? = null,
        @Query("state") state: String? = null,
        @Query("board") board: String? = null,
        @Query("permission_given") permission: String? = null,
        @Query("uploaded_by") uploadedBy: String? = null,
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ApiResponse<List<SchoolDataResponse>>

    @GET("admin/cities")
    suspend fun getCityList() : ApiResponse<List<String>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserData>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<UserData>

    @GET("auth/get_users")
    suspend fun getUsers(): ApiResponse<List<UserData>>

    @POST("auth/forgot_password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<ForgotPasswordData>

    @POST("auth/reset_password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiResponse<Nothing>

}