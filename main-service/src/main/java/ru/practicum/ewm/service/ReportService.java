package ru.practicum.ewm.service;

import ru.practicum.ewm.model.enums.ReportStatus;

import java.util.List;

public interface ReportService {
    void addReport(Long commentId);
    void deleteReport(Long reportId);
    List<Long> getListReportsByStatus(ReportStatus status);
}
