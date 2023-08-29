package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.dto.comment.UpdateReportDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Report;
import ru.practicum.ewm.model.enums.ReportStatus;
import ru.practicum.ewm.model.mappers.ReportMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.ReportRepository;
import ru.practicum.ewm.service.ReportService;

import java.time.LocalDateTime;
import java.util.List;


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
        report.setComment(comment);
        reportRepository.save(report);
        return ReportMapper.toReportDto(report);
    }

    @Override
    public List<Report> getListReportsByStatusNew() {
        return reportRepository.findNewReports();
    }


    @Override
    public ReportDto updateReport(Long reportId, UpdateReportDto updateReportDto) {
        Report report = checkReport(reportId);
        Comment comment = report.getComment();
        if (updateReportDto.getReportStatus().equals(ReportStatus.APPROVED)) {
            commentRepository.deleteById(comment.getId());
            report.setStatus(ReportStatus.APPROVED);
        } else if (updateReportDto.getReportStatus().equals(ReportStatus.REJECTED)) {
            report.setStatus(ReportStatus.REJECTED);
        } else {
            throw new UncorrectedParametersException("Такого статуса у жалобы не существует");
        }
        return ReportMapper.toReportDto(report);
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