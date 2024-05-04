package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ContextConfiguration(classes = {CategoryMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class CategoryMapperTests {
    @Autowired
    private CategoryMapper categoryMapper;


    @Test
    public void testToEntity_Successful() {
        //Arrange
        CategoryDto categoryDto= CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .build();
        Category target = new Category();

        //Act
        target = categoryMapper.toEntity(categoryDto);

        //Assert
        assertEquals(categoryDto.getName(), target.getName());
        assertEquals(categoryDto.getBudgetAmount(), target.getBudgetAmount(), .001);
        assertEquals(categoryDto.getBudgetAllocationPercentage(), target.getBudgetAllocationPercentage(), .001);
        assertNull(target.getCategoryType()); //validate that category type was not mapped
    }
}
