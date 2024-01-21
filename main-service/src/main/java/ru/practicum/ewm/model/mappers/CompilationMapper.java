package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.compilatoin.CompilationDto;
import ru.practicum.ewm.dto.compilatoin.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;

import java.util.stream.Collectors;

/**
 * Класс CompilationMapper служит для преобразования объектов Compilation и связанных с ними DTO.
 */
@UtilityClass
public class CompilationMapper {

    /**
     * Преобразует объект Compilation в объект CompilationDto.
     *
     * @param compilation объект Compilation для преобразования
     * @return объект CompilationDto, полученный в результате преобразования
     */
    public CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    /**
     * Преобразует объект NewCompilationDto в объект Compilation.
     *
     * @param compilationDto объект NewCompilationDto для преобразования
     * @return объект Compilation, полученный в результате преобразования
     */
    public Compilation toCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }
}