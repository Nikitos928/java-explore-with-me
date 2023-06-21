package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> mapToListCategoryDto(Page<Category> categories) {
        List<CategoryDto> categoryDtos = categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
        return categoryDtos;
    }

    public static Category mapToCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static Category mapToNewCategory(CategoryNewDto categoryNewDto) {
        Category category = new Category();
        category.setName(categoryNewDto.getName());
        return category;
    }
}
