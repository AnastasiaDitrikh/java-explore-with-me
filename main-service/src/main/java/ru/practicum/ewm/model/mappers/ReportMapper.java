package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Report;

@UtilityClass
public class ReportMapper {
    public ReportDto toReportDto(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .reason(report.getReason())
                .created(report.getCreated())
                .status(report.getStatus())
                .build();
    }

    public Report toReport(NewReportDto reportDto, Comment comment) {
        return Report.builder()
                .reason(reportDto.getReason())
                .comment(comment)
                .build();
    }
}