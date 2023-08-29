package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.dto.comment.UpdateReportDto;
import ru.practicum.ewm.model.Report;
import ru.practicum.ewm.service.ReportService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {
    private final ReportService reportService;

    @PostMapping("/reports/{reportId}")
    public ReportDto updateReportStatus(@PathVariable Long reportId,
                                        @RequestBody UpdateReportDto updateReportDto) {
        log.info("Post запрос на обновление статуса жалобы с id = {} ", reportId);
        return reportService.updateReport(reportId, updateReportDto);
    }

    @GetMapping("/reports")
    public List<Report> getListReportsByStatus() {
        log.info("GET запрос на получение жалоб со статусом New");
        return reportService.getListReportsByStatusNew();
    }
}