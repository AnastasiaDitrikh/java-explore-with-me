package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    /**
     * Получает список категорий со страницами.
     *
     * @param from начальная позиция списка (по умолчанию: 0, значение должно быть неотрицательным)
     * @param size размер страницы (по умолчанию: 10, значение должно быть положительным)
     * @return список объектов CategoryDto с информацией о категориях
     */
    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение категорий списком с размерами");
        return categoryService.getCategories(from, size);
    }

    /**
     * Получает информацию о категории по ее идентификатору.
     *
     * @param catId идентификатор категории
     * @return объект CategoryDto с информацией о категории
     */
    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("GET запрос на получение категории с id={}", catId);
        return categoryService.getCategoryById(catId);
    }
}