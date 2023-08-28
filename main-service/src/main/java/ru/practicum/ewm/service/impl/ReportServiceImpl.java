package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Report;
import ru.practicum.ewm.model.enums.ReportStatus;
import ru.practicum.ewm.model.mappers.ReportMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.ReportRepository;
import ru.practicum.ewm.service.ReportService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    @Override
    public ReportDto addReport(Long commentId, NewReportDto reportDto) {
        Comment comment = checkComment(commentId);
        Report report = ReportMapper.toReport(reportDto, comment);
        report.setCreated(LocalDateTime.now());
        report.setStatus(ReportStatus.NEW);
        reportRepository.save(report);
        return ReportMapper.toReportDto(report);
    }

    @Override
    public void deleteReport(Long reportId) {
        reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException(
                String.format("Жалоба c id=%d  не найдена", reportId)));
        reportRepository.deleteById(reportId);
    }

    @Override
    public List<Long> getListReportsByStatus(ReportStatus status) {
        List<Report> reports = reportRepository.findAllByStatus(status);
        List<Long> reportIds = new ArrayList<>();
        if (reports != null) {
            reportIds = reports.stream().map(Report::getId).collect(Collectors.toList());
        }
        return reportIds;
    }

    @Override
    public ReportDto updateReport(Long commentId, Long reportId) {
        Comment comment = checkComment(commentId);
        Report report = checkReport(reportId);


        return null;
    }


    private Comment checkComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментарий c id=%d  не найден", id)));
    }

    private Report checkReport(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Жалобы c id=%d  не найдена", id)));
    }
}
