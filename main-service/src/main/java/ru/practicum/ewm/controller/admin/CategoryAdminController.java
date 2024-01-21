package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Контроллер для администрирования категорий.
 */
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    /**
     * Обрабатывает POST запрос на создание новой категории.
     *
     * @param newCategoryDto - объект данных новой категории
     * @return объект CategoryDto с информацией о созданной категории
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST запрос на создание новой категории: {}", newCategoryDto);
        return categoryService.addNewCategory(newCategoryDto);
    }

    /**
     * Обрабатывает DELETE запрос на удаление категории по ее ID.
     *
     * @param catId - ID категории, которую нужно удалить
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("DELETE запрос на удалении категории с id =  {} ", catId);
        categoryService.deleteCategoryById(catId);
    }

    /**
     * Обрабатывает PATCH запрос на обновление категории по ее ID.
     *
     * @param catId       - ID категории, которую нужно обновить
     * @param categoryDto - объект данных обновленной категории
     * @return объект CategoryDto с информацией об обновленной категории
     */
    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable(value = "catId") @Min(1) Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("PATCH запрос на обновдение категории с id = {}", catId);
        return categoryService.updateCategory(catId, categoryDto);
    }
}