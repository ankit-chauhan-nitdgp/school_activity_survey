package com.hajmola.up.repositories

import com.hajmola.up.data.ApprovingAuthority
import com.hajmola.up.data.ListFetchingFilters
import com.hajmola.up.data.PrincipalInfo
import com.hajmola.up.data.SchoolDataResponse
import com.hajmola.up.data.SchoolDetailsData
import com.hajmola.up.data.SchoolInfo
import com.hajmola.up.data.ScpData
import com.hajmola.up.data.auth.UserData
import com.hajmola.up.utils.AppConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor() {
    //store school details locally before upload
    private val emptySchoolData = SchoolDetailsData(
        school = SchoolInfo(
            "", "","", AppConstants.SELECT_CITY, "", "", "",
            "", "", "", "", "", "", null, null
        ),
        principal = PrincipalInfo("", "", ""),
        approvingAuth = ApprovingAuthority("", "", "")
    )
    private var _schoolDetails: SchoolDetailsData? = emptySchoolData

    val schoolDetails: SchoolDetailsData? get() = _schoolDetails
    val getEmptySchoolDetails: SchoolDetailsData? get() = emptySchoolData

    fun updateSchoolDetails(update: (SchoolDetailsData) -> SchoolDetailsData) {
        val current = _schoolDetails
        if (current != null) {
            _schoolDetails = update(current)
        }
    }

    private var _cityList: List<String>? = emptyList()

    val cityList: List<String>? get() = _cityList

    fun updateCityList(update: (List<String>) -> List<String>) {
        val current = _cityList
        if (current != null) {
            _cityList = update(current)
        }
    }

    // store scp data locally before uploading
    private val emptyScpData = ScpData(
        "",AppConstants.YES, "", "", "", "", "", AppConstants.CLASSROOM,
        null, null, null, null, null, null, null, null
    )
    private var _scpDetails: ScpData? = emptyScpData

    val scpDetails: ScpData? get() = _scpDetails
    val getEmptyScpData: ScpData? get() = emptyScpData


    fun updateScpDetails(update: (ScpData) -> ScpData) {
        val current = _scpDetails
        if (current != null) {
            _scpDetails = update(current)
        }
    }

    // list to store fetched school list
    private var _fetchedSchoolList: List<SchoolDataResponse> = emptyList()

    val schoolList: List<SchoolDataResponse> get() = _fetchedSchoolList

    fun updateSchoolList(update: (List<SchoolDataResponse>) -> List<SchoolDataResponse>) {
        _fetchedSchoolList = update(_fetchedSchoolList)

    }

    //select school to show detailed data
    private var _selectedSchoolData: SchoolDataResponse? = SchoolDataResponse(-1,"","","","","","","","","",
        "","","",
        "","","",
        "",
        "", "","","","","", "",
        "","",
        null,null,null,null,null,null,null,"",
        null,null,"","")

    val selectedSchoolData: SchoolDataResponse? get() = _selectedSchoolData

    fun updateSelectedSchool(update: (SchoolDataResponse) -> SchoolDataResponse) {
        val current = _selectedSchoolData
        if (current != null) {
            _selectedSchoolData = update(current)
        }
    }


    //filters to fetch school data list
    private var _fetchFilters: ListFetchingFilters? = ListFetchingFilters(null,null,null,null,null)

    val fetchFilters: ListFetchingFilters? get() = _fetchFilters

    fun updateFetchFilters(update: (ListFetchingFilters) -> ListFetchingFilters) {
        val current = _fetchFilters
        if (current != null) {
            _fetchFilters = update(current)
        }
    }

    //filters to fetch school data list
    private var _allUsers: List<UserData> = emptyList()

    val getAllUsers: List<UserData> get() = _allUsers

    fun updateAllUsers(update: (List<UserData>) -> List<UserData>) {
        val current = _allUsers
        if (current != null) {
            _allUsers = update(current)
        }
    }

}