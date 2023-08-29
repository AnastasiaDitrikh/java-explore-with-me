package ru.practicum.ewm.dto.comment;


import lombok.*;
import ru.practicum.ewm.model.enums.ReportStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReportDto {
    @NotNull
    Long id;
    @NotBlank
    private ReportStatus reportStatus;
}