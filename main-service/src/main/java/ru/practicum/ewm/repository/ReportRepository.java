package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Report;
import ru.practicum.ewm.model.enums.ReportStatus;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByStatus(ReportStatus status);

    @Query(value = "SELECT * " +
            "FROM reports r " +
            "WHERE r.status = 'NEW'", nativeQuery = true)
    List<Report> findNewReports();
}

