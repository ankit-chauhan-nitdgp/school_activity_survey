package com.hajmola.up.excelhelper

import android.content.Context
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import com.hajmola.up.R

object SheetsServiceFactory {
    fun create(context: Context): Sheets {
//        val inputStream = context.assets.open("service_account.json")
//        val credentials = ServiceAccountCredentials.fromStream(inputStream)
//            .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))

        val credentials = context.resources.openRawResource(R.raw.credentials)
        val serviceAccount = ServiceAccountCredentials.fromStream(credentials)

        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        return Sheets.Builder(
            transport,
            jsonFactory,
            HttpCredentialsAdapter(serviceAccount)
        )
            .setApplicationName("SchoolApp")
            .build()
    }
}
