package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.List;


public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
}