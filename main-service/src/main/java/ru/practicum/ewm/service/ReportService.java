package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.model.enums.ReportStatus;

import java.util.List;

public interface ReportService {
    ReportDto addReport(Long commentId, NewReportDto reportDto);

    void deleteReport(Long reportId);

    List<Long> getListReportsByStatus(ReportStatus status);

    ReportDto updateReport(Long commentId, Long reportId);
}
