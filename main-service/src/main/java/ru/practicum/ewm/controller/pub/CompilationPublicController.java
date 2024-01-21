package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilatoin.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    /**
     * Получает список подборок со страницами.
     *
     * @param pinned флаг "закреплено" (необязательный параметр)
     * @param from   начальная позиция списка (по умолчанию: 0, значение должно быть неотрицательным)
     * @param size   размер страницы (по умолчанию: 10, значение должно быть положительным)
     * @return список объектов CompilationDto с информацией о подборках
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    /**
     * Получает информацию о подборке по ее идентификатору.
     *
     * @param compId идентификатор подборки
     * @return объект CompilationDto с информацией о подборке
     */
    @GetMapping("/{compId}")
    public CompilationDto findByIdCompilation(@PathVariable Long compId) {
        log.info("GET запрос на удаление подборки событий");
        return compilationService.findByIdCompilation(compId);
    }
}