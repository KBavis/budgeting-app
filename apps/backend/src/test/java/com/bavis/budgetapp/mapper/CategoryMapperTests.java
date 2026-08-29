package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryVt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = {CategoryMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class CategoryMapperTests {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void testToResponseDto_Successful() {
        //Arrange
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .build();
        Category category = Category.builder()
                .categoryId(1L)
                .build();
        CategoryVt categoryVt = CategoryVt.builder()
                .category(category)
                .categoryType(categoryType)
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .build();

        //Act
        CategoryResponseDto responseDto = categoryMapper.toResponseDto(category, categoryVt);

        //Assert
        assertNotNull(responseDto);
        assertEquals(category.getCategoryId(), responseDto.getCategoryId());
        assertEquals(categoryVt.getName(), responseDto.getName());
        assertEquals(categoryVt.getBudgetAmount(), responseDto.getBudgetAmount(), .001);
        assertEquals(categoryVt.getBudgetAllocationPercentage(), responseDto.getBudgetAllocationPercentage(), .001);
        assertEquals(Long.valueOf(10L), responseDto.getCategoryTypeId());
    }
}
