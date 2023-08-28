package ru.practicum.ewm.dto.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewReportDto {
    @NotBlank
    @Size(min = 2, max = 1500)
    private String reason;
}