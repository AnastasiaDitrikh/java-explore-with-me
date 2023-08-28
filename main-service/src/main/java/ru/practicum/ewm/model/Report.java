package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.model.enums.ReportStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "reports")
public class Report {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "status")
    private ReportStatus status;
    @Column(name = "reason")
    private String reason;
}

