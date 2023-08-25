package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationDto {
    private Long id;
    private Set<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}