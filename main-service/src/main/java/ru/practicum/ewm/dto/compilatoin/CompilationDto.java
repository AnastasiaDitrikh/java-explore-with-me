package ru.practicum.ewm.dto.compilatoin;

import lombok.*;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}