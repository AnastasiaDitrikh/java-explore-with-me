package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilatoin.CompilationDto;
import ru.practicum.ewm.dto.compilatoin.NewCompilationDto;
import ru.practicum.ewm.dto.compilatoin.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto update);

    void deleteCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findByIdCompilation(Long compId);
}