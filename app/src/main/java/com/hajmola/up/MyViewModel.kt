package com.hajmola.up

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hajmola.up.data.DeleteSchoolRequest
import com.hajmola.up.data.ListFetchingFilters
import com.hajmola.up.data.Location
import com.hajmola.up.data.SchoolDataResponse
import com.hajmola.up.data.SchoolDetailsData
import com.hajmola.up.data.SchoolRequest
import com.hajmola.up.data.ScpData
import com.hajmola.up.data.auth.LoginRequest
import com.hajmola.up.data.auth.RegisterRequest
import com.hajmola.up.data.auth.School
import com.hajmola.up.data.SchoolInfo
import com.hajmola.up.data.auth.UserData
import com.hajmola.up.excelhelper.GoogleSheetsHelper
import com.hajmola.up.excelhelper.SheetsServiceFactory
import com.hajmola.up.networks.ApiResponse
import com.hajmola.up.repositories.DataRepository
import com.hajmola.up.repositories.UserRepository
import com.hajmola.up.utils.AppConstants
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import com.hajmola.up.utils.Utils.TAG
import com.hajmola.up.utils.Utils.getMultipartFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataRepo : DataRepository
) : ViewModel() {

    //before upload
    fun getSchoolDetailsData(): SchoolDetailsData? {
        return dataRepo.schoolDetails
    }

    fun getEmptySchoolData(): SchoolDetailsData? {
        return dataRepo.getEmptySchoolDetails
    }

    fun updateSchoolDetailsData(update: (SchoolDetailsData) -> SchoolDetailsData) {
        dataRepo.updateSchoolDetails(update)
    }

    //before upload
    fun getScpDetailsData(): ScpData? {
        return dataRepo.scpDetails
    }

    fun updateScpDetailsData(update: (ScpData) -> ScpData) {
        dataRepo.updateScpDetails(update)
    }

    fun getEmptyScpData(): ScpData? {
        return dataRepo.getEmptyScpData
    }

    //after upload
     fun getFetchedSchoolList(): List<SchoolDataResponse> {
        return dataRepo.schoolList
    }

    fun updateFetchedSchoolList(update: (List<SchoolDataResponse>) -> List<SchoolDataResponse>) {
        dataRepo.updateSchoolList(update)
    }

    fun getFetchedCityList(): List<String>? {
        return dataRepo.cityList
    }

    fun updateFetchedCityList(update: (List<String>) -> List<String>) {
        dataRepo.updateCityList(update)
    }


    //after
     fun getSelectedSchoolData(): SchoolDataResponse? {
        return dataRepo.selectedSchoolData
    }

    fun updateSelectedSchoolData(update: (SchoolDataResponse) -> SchoolDataResponse) {
        dataRepo.updateSelectedSchool(update)
    }

    //filter
     fun getSelectedFilters(): ListFetchingFilters? {
        return dataRepo.fetchFilters
    }

    fun updateSelectedFilters(update: (ListFetchingFilters) -> ListFetchingFilters) {
        dataRepo.updateFetchFilters(update)
    }

    //update all users
    fun getAllUsersList(): List<UserData> {
        return dataRepo.getAllUsers
    }

    fun updateAllUsers(update: (List<UserData>) -> List<UserData>) {
        dataRepo.updateAllUsers(update)
    }


    private suspend fun <T> safeApiCall(
        state: MutableStateFlow<UiState<T>>,
        apiCall: suspend () -> ApiResponse<T>
    ) {
        state.value = UiState.Loading
        try {
            val response = apiCall()
            Log.d(TAG, "api response ${response.status}")
            Log.d(TAG, "api response meassage ${response.message}")
            if (response.status == "success" && response.data != null) {
                state.value = UiState.Success(response.data)
            } else {
                state.value = UiState.Error(response.message)
            }
        } catch (e: Exception) {
            Log.d(TAG, "api response catch ${e.message}")
            state.value = UiState.Error(e.message ?: "Unknown error")
        }
    }

    private val _cityNameState = MutableStateFlow(Location())
    val cityNameState: StateFlow<Location> = _cityNameState.asStateFlow()

    fun updateCityNameStateFlow(city: String){
        _cityNameState.update {
            it.copy(
                cityName = city
            )
        }
    }

    fun updateSchoolNameStateFlow(school: String){
        _cityNameState.update {
            it.copy(
                schoolName = school
            )
        }
    }


//    private val _testUsersState = MutableLiveData<UiState<List<String>>>()
//    val testUsersState: LiveData<UiState<List<String>>> = _testUsersState
//
//    fun loadTestUsers(){
//        viewModelScope.launch {
//            safeApiCall(_testUsersState){
//                repository.getUsers()
//            }
//        }
//    }

    private val _loginUserState = MutableStateFlow<UiState<UserData>>(UiState.Idle)
    val loginUserState: StateFlow<UiState<UserData>> = _loginUserState

    fun loginUser(request: LoginRequest){
        viewModelScope.launch {
            safeApiCall(_loginUserState){
                repository.login(request)
            }
        }
    }

    private val _registerUserState = MutableStateFlow<UiState<UserData>>(UiState.Idle)
    val registerUserState: StateFlow<UiState<UserData>> = _registerUserState

    fun registerUser(request: RegisterRequest){
        viewModelScope.launch {
            safeApiCall(_registerUserState){
                repository.register(request)
            }
        }
    }

    private val _getAllUserState = MutableStateFlow<UiState<List<UserData>>>(UiState.Idle)
    val getAllUserState: StateFlow<UiState<List<UserData>>> = _getAllUserState
    
    fun getAllUsers(){
        viewModelScope.launch {
            safeApiCall(_getAllUserState){
                repository.getUsers()
            }
        }
    }

    private val _getCityListState = MutableStateFlow<UiState<List<String>>>(UiState.Idle)
    val getCityListState: StateFlow<UiState<List<String>>> = _getCityListState

    fun getCityList(){
        viewModelScope.launch {
            safeApiCall(_getCityListState){
                repository.getCityList()
            }

            launch {
                delay(2000)
                Log.d(TAG,"vm cityList State : ${getCityListState.value}")
            }
        }
    }

    private val _schoolListByCityState = MutableStateFlow<UiState<List<School>>>(UiState.Idle)
    val schoolListByCityState: StateFlow<UiState<List<School>>> = _schoolListByCityState

    fun getSchoolByCity(city: String){
        Log.d(TAG, "getSchoolByCity called $city")
        viewModelScope.launch {
            safeApiCall(_schoolListByCityState){
                repository.getSchoolByCity(city)
            }
        }
    }

    private val _addSchoolState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val addSchoolState: StateFlow<UiState<Unit>> = _addSchoolState

    fun addSchool(request: SchoolRequest){
        viewModelScope.launch {
            safeApiCall(_addSchoolState){
                repository.addSchool(request)
            }
        }
    }

    private val _uploadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uploadState: StateFlow<UiState<Unit>> = _uploadState

    fun uploadSchoolInfo(context: Context) {
        val school = getSchoolDetailsData()?.school ?: return
        val principal = getSchoolDetailsData()?.principal ?: return
        val authority = getSchoolDetailsData()?.approvingAuth ?: return
        val scp = getScpDetailsData() ?: return

        viewModelScope.launch {
            _uploadState.value = UiState.Loading

            try {
                val stringFields = mapOf(
                    "name" to school.name,
                    "address" to school.addressLine1 +" " +school.addressLine2,
                    "city" to school.city,
                    "state" to school.state,
                    "pin_code" to school.pinCode,
                    "email_id" to school.emailId,
                    "board" to school.board,
                    "up_to_class" to school.upToClass,
                    "total_strength" to school.totalStrength,
                    "strength_8to12" to school.strength8to12,
                    "avg_section_per_class" to school.avgSectionPerClass,
                    "avg_tution_fees" to school.avgTutionFees,
                    "principal_name" to principal.name,
                    "principal_contact" to principal.contact,
                    "principal_email" to principal.email,
                    "authority_name" to authority.name,
                    "authority_contact" to authority.contact,
                    "authority_email" to authority.email,
                    "permission_given" to scp.permissionGiven,
                    "activity_date" to scp.activityDate,
                    "estimated_sessions" to scp.estimatedSessions,
                    "activity_start_time" to scp.activityStartTime,
                    "activity_end_time" to scp.activityEndTime,
                    "units_distributed" to scp.unitsDistributed,
                    "activity_area" to scp.activityArea,
                    "uploaded_by" to SessionManager.getUploader(context)!! // can be logged-in user
                ).mapValues { (_, v) ->
                    v.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                val fileParts = mutableListOf<MultipartBody.Part>()
                val fileMap = getFileMap(school, scp)

                fileMap.forEach { (field, uri) ->
                    uri?.let {
                        try {
                            val part = getMultipartFromUri(context, it, field)
                            fileParts.add(part)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                val response = repository.uploadSchool(stringFields, fileParts)
                if (response.status == "success") {
                    // Fetch updated school list and sync to Excel sheet after successful upload
                    try {
                        val schoolResponse = repository.getSchools()
                        if (schoolResponse.status == "success" && schoolResponse.data != null) {
                            withContext(Dispatchers.IO) {
                                val sheetsService = SheetsServiceFactory.create(context)
                                GoogleSheetsHelper.appendSchoolsToSheet(
                                    sheetsService,
                                    AppConstants.SPREADSHEET_ID,
                                    schoolResponse.data
                                )
                            }
                            Log.d(TAG, "Successfully updated Excel sheet after school upload")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating Excel sheet: ${e.message}")
                    }finally {
                        _uploadState.value = UiState.Success(Unit)
                    }
                } else {
                    _uploadState.value = UiState.Error(response.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "uploadSchoolInfo exception: ${e.message}")
                _uploadState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun getFileMap(school: SchoolInfo, scp: ScpData): MutableMap<String, Uri?>{
        val fileMap = mutableMapOf<String,Uri?>()

        if (school.uriFront != null){
            fileMap[AppConstants.SCHOOL_FRONT_PHOTO] = school.uriFront
        }
        if (school.uriBack != null){
            fileMap[AppConstants.SCHOOL_BACK_PHOTO] = school.uriBack
        }
        if (scp.activity1Uri != null){
            fileMap[AppConstants.ACTIVITY_1_PHOTO] = scp.activity1Uri
        }
        if (scp.activity2Uri != null){
            fileMap[AppConstants.ACTIVITY_2_PHOTO] = scp.activity2Uri
        }
        if (scp.activity3Uri != null){
            fileMap[AppConstants.ACTIVITY_3_PHOTO] = scp.activity3Uri
        }
        if (scp.activity4Uri != null){
            fileMap[AppConstants.ACTIVITY_4_PHOTO] = scp.activity4Uri
        }
        if (scp.activity5Uri != null){
            fileMap[AppConstants.ACTIVITY_5_PHOTO] = scp.activity5Uri
        }
        if (scp.activity6Uri != null){
            fileMap[AppConstants.ACTIVITY_6_PHOTO] = scp.activity6Uri
        }
        if (scp.acknowledgementLetterUri != null){
            fileMap[AppConstants.ACKNOWLEDGE_LETTER] = scp.acknowledgementLetterUri
        }
        if (scp.permissionLetterUri != null){
            fileMap[AppConstants.PERMISSION_LETTER] = scp.permissionLetterUri
        }

        return fileMap
    }
    // 1. School Response StateFlow
    private val _schoolResponseData = MutableStateFlow<UiState<List<SchoolDataResponse>>>(UiState.Idle)
    val schoolResponseData: StateFlow<UiState<List<SchoolDataResponse>>> = _schoolResponseData.asStateFlow()

    // 2. Excel Update StateFlow
    private val _excelUpdate = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val excelUpdate: StateFlow<UiState<Unit>> = _excelUpdate.asStateFlow()

    fun fetchSchools(
        city: String? = null,
        uploadedBy: String? = null,
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _schoolResponseData.value = UiState.Loading
            // Assuming your safeApiCall can handle MutableStateFlow or you can update it manually:
            safeApiCall(_schoolResponseData) {
                repository.getSchools(city, uploadedBy, date, startDate, endDate)
            }
        }
    }

    fun fetchAndUploadSchools(context: Context) {
        viewModelScope.launch {
            _excelUpdate.value = UiState.Loading
            try {
                val response = repository.getSchools()
                if (response.status == "success" && response.data != null) {
                    withContext(Dispatchers.IO) {
                        val sheetsService = SheetsServiceFactory.create(context)
                        GoogleSheetsHelper.appendSchoolsToSheet(
                            sheetsService,
                            AppConstants.SPREADSHEET_ID,
                            response.data
                        )
                    }
                    Log.d("AdminVM", "✅ Uploaded ${response.data.size} schools")
                }
                Log.d("AdminVM", "✅ Uploaded schools to Google Sheets")
                _excelUpdate.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _excelUpdate.value = UiState.Error("Upload failed: ${e.message}")
            }
        }
    }

    fun cleanAndUploadSchoolsToExcel(context: Context, spreadsheetId: String = AppConstants.SPREADSHEET_ID) {
        viewModelScope.launch {
            _excelUpdate.value = UiState.Loading
            try {
                val response = repository.getSchools()
                if (response.status == "success" && response.data != null) {
                    withContext(Dispatchers.IO) {
                        val sheetsService = SheetsServiceFactory.create(context)
                        GoogleSheetsHelper.clearAndUploadSchoolsToSheet(
                            sheetsService,
                            spreadsheetId,
                            response.data
                        )
                    }
                    Log.d("AdminVM", "✅ Cleaned and uploaded ${response.data.size} schools to Google Sheets")
                    _excelUpdate.value = UiState.Success(Unit)
                } else {
                    _excelUpdate.value = UiState.Error("Failed to fetch schools: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AdminVM", "Error cleaning and uploading to Excel: ${e.message}")
                _excelUpdate.value = UiState.Error("Upload failed: ${e.message}")
            }
        }
    }

    private val _deleteSchoolUpdate = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteSchoolUpdate: StateFlow<UiState<Unit>> = _deleteSchoolUpdate.asStateFlow()
    fun deleteSchool(id: DeleteSchoolRequest){
        viewModelScope.launch {
            safeApiCall(_deleteSchoolUpdate) {
                repository.deleteSchool(id)
            }
        }
    }

    // 3. Clear states so they don't re-trigger on Back Press
    fun resetExcelUploadStates() {
        _schoolResponseData.value = UiState.Idle
        _excelUpdate.value = UiState.Idle
    }

    fun resetUploadState() {
        _uploadState.value = UiState.Idle
    }

    fun resetRegisterState() {
        _registerUserState.value = UiState.Idle
    }

}