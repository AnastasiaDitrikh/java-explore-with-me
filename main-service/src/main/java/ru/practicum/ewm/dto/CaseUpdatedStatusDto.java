package ru.practicum.ewm.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseUpdatedStatusDto {

    private List<Long> idsFromUpdateStatus;
    private List<Long> processedIds;
}