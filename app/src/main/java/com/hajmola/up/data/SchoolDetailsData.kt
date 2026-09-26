package com.hajmola.up.data

import com.hajmola.up.data.SchoolInfo

data class SchoolDetailsData(
    var school: SchoolInfo,
    var principal: PrincipalInfo,
    var approvingAuth: ApprovingAuthority
)
