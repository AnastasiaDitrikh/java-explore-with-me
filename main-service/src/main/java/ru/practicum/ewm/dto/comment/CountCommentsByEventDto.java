package ru.practicum.ewm.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountCommentsByEventDto {

    private Long eventId;
    private Long countComments;
}