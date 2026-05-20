package com.weathersnap.ui.saved

import androidx.lifecycle.ViewModel
import com.weathersnap.data.repository.ReportRepository
import com.weathersnap.domain.model.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SavedReportsViewModel @Inject constructor(
    reportRepository: ReportRepository
) : ViewModel() {

    val reports: Flow<List<Report>> = reportRepository.getAllReports()
    val reportCount: Flow<Int> = reportRepository.getReportCount()
}
