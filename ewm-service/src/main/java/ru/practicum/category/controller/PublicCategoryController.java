package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("API PublicCategory. GET: параметры from = {}, size = {}", from, size);
        List<CategoryDto> categoryDtos = categoryService.getCategories(from, size);
        log.info("API PublicCategory. GET: найдено категорий - {}: {}", categoryDtos.size(), categoryDtos);
        return categoryDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable int catId) {
        CategoryDto categoryDto = categoryService.getCategoryById(catId);
        log.info("API PublicCategory. GET:  найдена категория {}, catId={}", categoryDto, catId);
        return categoryDto;
    }
}
