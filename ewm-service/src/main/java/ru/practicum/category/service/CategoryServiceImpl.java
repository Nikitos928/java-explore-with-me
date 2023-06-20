package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Sort startSort = Sort.by("name");
        Pageable pageable = FromSizeRequest.of(from, size, startSort);
        Page<Category> categories = categoryRepository.findAll(pageable);
        log.info("CategoryService: Данные о всех категориях, сортировка по name");
        return CategoryMapper.mapToListCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(int catId) {
        Category category = checkingExistCategory(catId);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto saveNewCategory(CategoryNewDto categoryNewDto) {
        Category newCategory = categoryRepository.save(CategoryMapper.mapToNewCategory(categoryNewDto));
        log.info("CategoryService: Добавлена категория: {}", newCategory);
        return CategoryMapper.mapToCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(int catId, CategoryNewDto categoryNewDto) {
        Category updateCategory = checkingExistCategory(catId);
        updateCategory.setName(categoryNewDto.getName());
        //categoryRepository.save(updateCategory);
        return CategoryMapper.mapToCategoryDto(updateCategory);
    }

    @Transactional
    @Override
    public void deleteCategoryById(int catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id=%s не найдена", catId));
        }

        if (eventRepository.findEventByCategoryId(catId).size() == 0) {
            log.info("Удалена категория с id = {}", catId);
            categoryRepository.deleteById(catId);
        } else {
            log.error("Нельзя удалить категорию, в которой есть события");
            throw new ConflictException("Нельзя удалить категорию, в которой есть события");
        }
    }

    private Category checkingExistCategory(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%s не найдена", catId)));
    }
}
