package com.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query("SELECT * FROM reports WHERE isDraft = 0 ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT COUNT(*) FROM reports WHERE isDraft = 0")
    fun getReportCount(): Flow<Int>

    @Query("SELECT * FROM reports WHERE isDraft = 1 LIMIT 1")
    suspend fun getDraftReport(): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReport(id: Long)

    @Query("DELETE FROM reports WHERE isDraft = 1")
    suspend fun deleteAllDrafts()
}
