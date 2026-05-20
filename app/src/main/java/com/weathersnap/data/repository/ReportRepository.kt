package com.weathersnap.data.repository

import com.weathersnap.data.local.ReportDao
import com.weathersnap.data.local.ReportEntity
import com.weathersnap.domain.model.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getReportCount(): Flow<Int> = reportDao.getReportCount()

    suspend fun saveReport(report: Report): Long = withContext(Dispatchers.IO) {
        reportDao.insertReport(report.toEntity())
    }

    suspend fun saveDraft(report: Report): Long = withContext(Dispatchers.IO) {
        // Clear any existing drafts first to prevent duplicates
        reportDao.deleteAllDrafts()
        reportDao.insertReport(report.toEntity().copy(isDraft = true))
    }

    suspend fun getDraft(): Report? = withContext(Dispatchers.IO) {
        reportDao.getDraftReport()?.toDomain()
    }

    suspend fun finalizeDraft(draftId: Long, report: Report) = withContext(Dispatchers.IO) {
        // Delete the draft
        reportDao.deleteReport(draftId)
        // Insert final report
        reportDao.insertReport(report.toEntity().copy(isDraft = false))
    }

    suspend fun deleteDraftAndCleanup() = withContext(Dispatchers.IO) {
        val draft = reportDao.getDraftReport()
        if (draft != null) {
            // Clean up temp image file
            if (draft.imagePath.isNotEmpty()) {
                val file = File(draft.imagePath)
                if (file.exists()) file.delete()
            }
            reportDao.deleteAllDrafts()
        }
    }

    private fun ReportEntity.toDomain() = Report(
        id = id,
        cityName = cityName,
        country = country,
        temperature = temperature,
        condition = condition,
        humidity = humidity,
        windSpeed = windSpeed,
        pressure = pressure,
        imagePath = imagePath,
        originalSizeBytes = originalSizeBytes,
        compressedSizeBytes = compressedSizeBytes,
        notes = notes,
        timestamp = timestamp,
        isDraft = isDraft
    )

    private fun Report.toEntity() = ReportEntity(
        id = if (id == 0L) 0 else id,
        cityName = cityName,
        country = country,
        temperature = temperature,
        condition = condition,
        humidity = humidity,
        windSpeed = windSpeed,
        pressure = pressure,
        imagePath = imagePath,
        originalSizeBytes = originalSizeBytes,
        compressedSizeBytes = compressedSizeBytes,
        notes = notes,
        timestamp = timestamp,
        isDraft = isDraft
    )
}
