package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.model.Report;

@UtilityClass
public class ReportMapper {
    public ReportDto toReportDto(Report report) {
        return ReportDto.builder()
                .commentDto(CommentMapper.toCommentDto(report.getComment()))
                .build();
    }

    public Report toReport(Long commentId, ReportDto reportDto) {
        return Report.builder()
                .comment(CommentMapper.toComment(reportDto.getCommentDto()))
                .build();
    }
}