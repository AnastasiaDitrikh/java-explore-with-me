package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.mappers.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        List<Event> events = Optional.ofNullable(compilationDto.getEvents())
                .map(eventRepository::findAllById)
                .orElse(Collections.emptyList());
        compilation.setEvents(events);

        Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto update) {
        Compilation compilation = checkCompilation(compId);

        List<Event> events = Optional.ofNullable(update.getEvents())
                .map(ids -> ids.stream()
                        .flatMap(id -> eventRepository.findAllById(Collections.singleton(id)).stream())
                        .collect(Collectors.toList())
                )
                .orElse(compilation.getEvents());
        compilation.setEvents(events);

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));
        compilation.setTitle(Optional.ofNullable(update.getTitle()).orElse(compilation.getTitle()));

        Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return compilationRepository.findAllByPinnedIs(pinned, pageable)
                .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findByIdCompilation(Long compId) {
        return CompilationMapper.toDto(checkCompilation(compId));
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation с id = " + compId + " не найден"));
    }
}