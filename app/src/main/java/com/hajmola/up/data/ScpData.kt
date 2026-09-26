package com.hajmola.up.data

import android.net.Uri

data class ScpData(
    var uploadedBy: String,
    var permissionGiven: String,
    var activityDate: String,
    var estimatedSessions: String,
    var activityStartTime: String,
    var activityEndTime: String,
    var unitsDistributed: String,
    var activityArea: String,

    var activity1Uri: Uri?,
    var activity2Uri: Uri?,
    var activity3Uri: Uri?,
    var activity4Uri: Uri?,
    var activity5Uri: Uri?,
    var activity6Uri: Uri?,
    var acknowledgementLetterUri: Uri?,
    var permissionLetterUri: Uri?
)
