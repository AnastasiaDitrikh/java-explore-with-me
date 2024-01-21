package ru.practicum.ewm.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDto {

    private Long id;
    private String name;
}