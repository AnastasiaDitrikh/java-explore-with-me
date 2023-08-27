package ru.practicum.ewm.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {
    private CommentDto commentDto;
}
