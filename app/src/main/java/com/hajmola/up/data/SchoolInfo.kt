package com.hajmola.up.data

import android.net.Uri

data class SchoolInfo(
    var name: String,
    var addressLine1: String,
    var addressLine2: String,
    var city: String,
    var state: String,
    var pinCode: String,
    var emailId: String,
    var board: String,
    var upToClass: String,
    var totalStrength: String,
    var strength8to12: String,
    var avgSectionPerClass: String,
    var avgTutionFees: String,

    var uriFront: Uri?,
    var uriBack: Uri?
)