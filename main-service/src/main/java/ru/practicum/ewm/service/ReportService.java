package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.dto.comment.UpdateReportDto;
import ru.practicum.ewm.model.Report;

import java.util.List;

public interface ReportService {
    ReportDto addReport(Long commentId, NewReportDto reportDto);

    List<Report> getListReportsByStatusNew();

    ReportDto updateReport(Long reportId, UpdateReportDto updateReportDto);
}
