package ru.practicum.ewm.dto.comment;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

    private LocalDateTime lastUpdatedOn;
}