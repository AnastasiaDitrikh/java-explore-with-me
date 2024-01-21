package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.Category;

/**
 * Класс CategoryMapper служит для преобразования объектов Category и связанных с ними DTO.
 */
@UtilityClass
public class CategoryMapper {

    /**
     * Преобразует объект CategoryDto в объект Category.
     *
     * @param categoryDto объект CategoryDto для преобразования
     * @return объект Category, полученный в результате преобразования
     */
    public Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    /**
     * Преобразует объект NewCategoryDto в объект Category.
     *
     * @param newCategoryDto объект NewCategoryDto для преобразования
     * @return объект Category, полученный в результате преобразования
     */
    public Category toNewCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    /**
     * Преобразует объект Category в объект CategoryDto.
     *
     * @param category объект Category для преобразования
     * @return объект CategoryDto, полученный в результате преобразования
     */
    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}