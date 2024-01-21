package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilatoin.CompilationDto;
import ru.practicum.ewm.dto.compilatoin.NewCompilationDto;
import ru.practicum.ewm.dto.compilatoin.UpdateCompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    /**
     * Обрабатывает POST запрос на создание новой подборки событий.
     *
     * @param compilationDto - объект данных новой подборки событий
     * @return объект CompilationDto с информацией о созданной подборке событий
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("POST запрос на создание подборки событий");
        return compilationService.addCompilation(compilationDto);
    }

    /**
     * Обрабатывает PATCH запрос на обновление подборки событий по ее ID.
     *
     * @param update - объект данных для обновления подборки событий
     * @param compId - ID подборки событий, которую нужно обновить
     * @return объект CompilationDto с обновленной информацией о подборке событий
     */
    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationDto update,
                                            @PathVariable Long compId) {
        log.info("PATCH запрос на обнавление подборки событий");
        return compilationService.updateCompilation(compId, update);
    }

    /**
     * Обрабатывает DELETE запрос на удаление подборки событий по ее ID.
     *
     * @param compId - ID подборки событий, которую нужно удалить
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE запрос на удаление подборки событий");
        compilationService.deleteCompilation(compId);
    }
}