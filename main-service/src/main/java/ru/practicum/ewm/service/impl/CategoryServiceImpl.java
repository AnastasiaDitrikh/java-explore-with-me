package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.mappers.CategoryMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventsRepository;

    /**
     * Возвращает список категорий с пагинацией.
     *
     * @param from начальная позиция списка
     * @param size размер списка
     * @return список объектов CategoryDto
     */
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    /**
     * Возвращает категорию по заданному идентификатору.
     *
     * @param catId идентификатор категории
     * @return объект CategoryDto
     * @throws NotFoundException если категория не найдена
     */
    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = checkCategory(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    /**
     * Добавляет новую категорию.
     *
     * @param newCategoryDto объект NewCategoryDto, содержащий данные новой категории
     * @return объект CategoryDto новой категории
     */
    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toNewCategoryDto(newCategoryDto);
        Category saveCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(saveCategory);
    }

    /**
     * Удаляет категорию по заданному идентификатору.
     *
     * @param catId идентификатор категории
     * @throws NotFoundException если категория не найдена
     * @throws ConflictException если категория используется в событиях
     */
    @Override
    public void deleteCategoryById(Long catId) {
        Category category = checkCategory(catId);
        List<Event> events = eventsRepository.findByCategory(category);
        if (!events.isEmpty()) {
            throw new ConflictException("Can't delete category due to using for some events");
        }
        categoryRepository.deleteById(catId);
    }

    /**
     * Обновляет информацию о категории по заданному идентификатору.
     *
     * @param catId       идентификатор категории
     * @param categoryDto объект CategoryDto с обновленными данными категории
     * @return объект CategoryDto обновленной категории
     * @throws NotFoundException если категория не найдена
     * @throws ConflictException если новое имя категории уже используется другой категорией
     */
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category oldCategory = checkCategory(catId);
        String newName = categoryDto.getName();

        if (newName != null && !oldCategory.getName().equals(newName)) {
            checkUniqNameCategoryIgnoreCase(newName);
        }

        oldCategory.setName(newName);
        Category updatedCategory = categoryRepository.save(oldCategory);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    /**
     * Проверяет уникальность имени категории (регистр не учитывается).
     *
     * @param name имя категории
     * @throws ConflictException если категория с таким именем уже существует
     */
    private void checkUniqNameCategoryIgnoreCase(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException(("Категория " + name + " уже существует"));
        }
    }

    /**
     * Проверяет существование категории по заданному идентификатору.
     *
     * @param catId идентификатор категории
     * @return объект Category категории
     * @throws NotFoundException если категория не найдена
     */
    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категории с id = " + catId + " не существует"));
    }
}