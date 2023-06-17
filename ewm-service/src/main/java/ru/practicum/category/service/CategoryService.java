package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(int catId);

    Category saveNewCategory(CategoryNewDto categoryNewDto);

    CategoryDto updateCategory(int catId, CategoryNewDto categoryNewDto);

    void deleteCategoryById(int catId);
}
