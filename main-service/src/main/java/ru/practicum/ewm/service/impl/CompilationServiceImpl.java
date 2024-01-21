package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilatoin.CompilationDto;
import ru.practicum.ewm.dto.compilatoin.NewCompilationDto;
import ru.practicum.ewm.dto.compilatoin.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.mappers.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    /**
     * Добавляет новую подборку
     *
     * @param compilationDto объект NewCompilationDto, содержащий данные новой подборки
     * @return объект CompilationDto новой подборки
     */
    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        Set<Long> compEventIds = (compilationDto.getEvents() != null) ? compilationDto.getEvents() : Collections.emptySet();
        List<Long> eventIds = new ArrayList<>(compEventIds);
        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Set<Event> eventsSet = new HashSet<>(events);
        compilation.setEvents(eventsSet);

        Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    /**
     * Обновляет информацию о подборке по заданному идентификатору.
     *
     * @param compId идентификатор подборки
     * @param update объект UpdateCompilationDto с обновленными данными подборки
     * @return объект CompilationDto обновленной подборки
     */
    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto update) {
        Compilation compilation = checkCompilation(compId);
        Set<Long> eventIds = update.getEvents();

        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
            Set<Event> eventSet = new HashSet<>(events);
            compilation.setEvents(eventSet);
        }

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));

        if (compilation.getTitle().isBlank()) {
            throw new UncorrectedParametersException("Title не может состоять из пробелов");
        }

        compilation.setTitle(Optional.ofNullable(update.getTitle()).orElse(compilation.getTitle()));
        return CompilationMapper.toDto(compilation);
    }

    /**
     * Удаляет подборку по заданному идентификатору.
     *
     * @param compId идентификатор подборки
     */
    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    /**
     * Возвращает список подборок с пагинацией и возможностью фильтрации по прикрепленности.
     *
     * @param pinned флаг прикрепленности (null - все подборки)
     * @param from   начальная позиция списка
     * @param size   размер списка
     * @return список объектов CompilationDto подборок
     */
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает подборку по заданному идентификатору.
     *
     * @param compId идентификатор подборки
     * @return объект CompilationDto подборки
     */
    @Override
    public CompilationDto findByIdCompilation(Long compId) {
        return CompilationMapper.toDto(checkCompilation(compId));
    }

    /**
     * Проверяет существование подборки по заданному идентификатору.
     *
     * @param compId идентификатор подборки
     * @return объект Compilation подборки
     * @throws NotFoundException если подборки не найдена
     */
    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation с id = " + compId + " не найден"));
    }
}