package com.hajmola.up.excelhelper

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import com.hajmola.up.data.SchoolDataResponse

object GoogleSheetsHelper {

    fun clearAndUploadSchoolsToSheet(
        sheetsService: Sheets,
        spreadsheetId: String,
        schools: List<SchoolDataResponse>
    ) {
        // Clear all existing data in Sheet1 to eliminate duplicates
        try {
            sheetsService.spreadsheets().values()
                .clear(spreadsheetId, "Sheet1!A1:AJ100000", ClearValuesRequest())
                .execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Deduplicate list by id or name/city/activity_date
        val uniqueSchools = schools.distinctBy {
            if (it.id > 0) it.id else "${it.name}_${it.city}_${it.activity_date}"
        }

        val values = ArrayList<List<Any>>()

        // Header row
        values.add(
            listOf(
                "Name", "Address", "City", "State", "Pin Code", "Email ID", "Board", "Up To Class",
                "Total Strength", "Strength 8-12", "Avg Section/Class", "Avg Tuition Fees",
                "Principal Name", "Principal Contact", "Principal Email",
                "Authority Name", "Authority Contact", "Authority Email",
                "Permission Given", "Activity Date", "Estimated Sessions",
                "Activity Start", "Activity End", "Units Distributed", "Activity Area", "Uploaded By",
                "Front Image", "Back Image",
                "Activity1 Image", "Activity2 Image", "Activity3 Image", "Activity4 Image",
                "Activity5 Image", "Activity6 Image",
                "Acknowledgement Letter", "Permission Letter"
            )
        )

        // Add each unique school row
        for (school in uniqueSchools) {
            values.add(
                listOf(
                    school.name ?: "",
                    school.address ?: "",
                    school.city ?: "",
                    school.state ?: "",
                    school.pin_code ?: "",
                    school.email_id ?: "",
                    school.board ?: "",
                    school.up_to_class ?: "",
                    school.total_strength ?: "",
                    school.strength_8to12 ?: "",
                    school.avg_section_per_class ?: "",
                    school.avg_tution_fees ?: "",
                    school.principal_name ?: "",
                    school.principal_contact ?: "",
                    school.principal_email ?: "",
                    school.authority_name ?: "",
                    school.authority_contact ?: "",
                    school.authority_email ?: "",
                    school.permission_given ?: "",
                    school.activity_date ?: "",
                    school.estimated_sessions ?: "",
                    school.activity_start_time ?: "",
                    school.activity_end_time ?: "",
                    school.units_distributed ?: "",
                    school.activity_area ?: "",
                    school.uploaded_by ?: "",
                    school.uri_front ?: "",
                    school.uri_back ?: "",
                    school.activity1_uri ?: "",
                    school.activity2_uri ?: "",
                    school.activity3_uri ?: "",
                    school.activity4_uri ?: "",
                    school.activity5_uri ?: "",
                    school.activity6_uri ?: "",
                    school.acknowledgement_letter_uri ?: "",
                    school.permission_letter_uri ?: ""
                )
            )
        }

        val body = ValueRange().setValues(values)
        sheetsService.spreadsheets().values()
            .update(spreadsheetId, "Sheet1!A1", body)
            .setValueInputOption("RAW")
            .execute()
    }

    suspend fun appendSchoolsToSheet(
        sheetsService: Sheets,
        spreadsheetId: String,
        schools: List<SchoolDataResponse>
    ) {
        val values = ArrayList<List<Any>>()

        // Header row (optional: only add once, or check if sheet empty before adding)
        values.add(
            listOf(
                "Name", "Address", "City", "State", "Pin Code", "Email ID", "Board", "Up To Class",
                "Total Strength", "Strength 8-12", "Avg Section/Class", "Avg Tuition Fees",
                "Principal Name", "Principal Contact", "Principal Email",
                "Authority Name", "Authority Contact", "Authority Email",
                "Permission Given", "Activity Date", "Estimated Sessions",
                "Activity Start", "Activity End", "Units Distributed", "Activity Area", "Uploaded By",
                "Front Image", "Back Image",
                "Activity1 Image", "Activity2 Image", "Activity3 Image", "Activity4 Image",
                "Activity5 Image", "Activity6 Image",
                "Acknowledgement Letter", "Permission Letter"
            )
        )

        // Add each school row
        for (school in schools) {
            values.add(
                listOf(
                    school.name ?: "",
                    school.address ?: "",
                    school.city ?: "",
                    school.state ?: "",
                    school.pin_code ?: "",
                    school.email_id ?: "",
                    school.board ?: "",
                    school.up_to_class ?: "",
                    school.total_strength ?: "",
                    school.strength_8to12 ?: "",
                    school.avg_section_per_class ?: "",
                    school.avg_tution_fees ?: "",
                    school.principal_name ?: "",
                    school.principal_contact ?: "",
                    school.principal_email ?: "",
                    school.authority_name ?: "",
                    school.authority_contact ?: "",
                    school.authority_email ?: "",
                    school.permission_given ?: "",
                    school.activity_date ?: "",
                    school.estimated_sessions ?: "",
                    school.activity_start_time ?: "",
                    school.activity_end_time ?: "",
                    school.units_distributed ?: "",
                    school.activity_area ?: "",
                    school.uploaded_by ?: "",
                    school.uri_front ?: "",
                    school.uri_back ?: "",
                    school.activity1_uri ?: "",
                    school.activity2_uri ?: "",
                    school.activity3_uri ?: "",
                    school.activity4_uri ?: "",
                    school.activity5_uri ?: "",
                    school.activity6_uri ?: "",
                    school.acknowledgement_letter_uri ?: "",
                    school.permission_letter_uri ?: ""
                )
            )
        }

//        val body = ValueRange().setValues(values)
//
//        sheetsService.spreadsheets().values()
//            .append(spreadsheetId, "Sheet1!A:AJ", body) // AJ = enough columns
//            .setValueInputOption("RAW")
//            .setInsertDataOption("INSERT_ROWS")
//            .execute()

        val body = ValueRange().setValues(values)
        sheetsService.spreadsheets().values()
            .update(spreadsheetId, "Sheet1!A1", body)
            .setValueInputOption("RAW")
            .execute()
    }

}
