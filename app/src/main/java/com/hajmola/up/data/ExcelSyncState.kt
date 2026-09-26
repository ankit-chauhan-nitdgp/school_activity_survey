package com.hajmola.up.data

sealed class ExcelSyncState {
    object Idle : ExcelSyncState()
    object Loading : ExcelSyncState()
    data class Success(
        val message: String
    ) : ExcelSyncState()
    data class Error(
        val message: String
    ) : ExcelSyncState()
}