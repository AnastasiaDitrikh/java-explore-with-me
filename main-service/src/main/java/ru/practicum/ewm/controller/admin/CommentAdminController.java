package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.Report;
import ru.practicum.ewm.model.enums.ReportStatus;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.ReportService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {
    private final ReportService reportService;
    private final CommentService commentService;

    @PostMapping("/reports/{reportId}")
    public Report updateReportStatus(@PathVariable Long reportId) {
        return null;
    }
    @GetMapping("/reports")
    public List<Long> getListReportsByStatus(@RequestBody ReportStatus status) {
        log.info("GET запрос на получение жалоб со статусом " + status);
        return reportService.getListReportsByStatus(status);
    }
}