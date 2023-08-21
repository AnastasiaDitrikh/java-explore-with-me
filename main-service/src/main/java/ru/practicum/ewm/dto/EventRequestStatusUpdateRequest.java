package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
