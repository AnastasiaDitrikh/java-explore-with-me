package ru.practicum.ewm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ViewStats {
    private String app;
    private String uri;
    private int hits;
}