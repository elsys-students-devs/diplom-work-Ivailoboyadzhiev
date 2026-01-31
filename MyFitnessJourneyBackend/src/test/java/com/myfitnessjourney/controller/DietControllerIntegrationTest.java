package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.service.DietService;
import com.myfitnessjourney.service.MealService;
import com.myfitnessjourney.mapper.MealMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DietController.class)
class DietControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DietService dietService;

    @MockBean
    private MealService mealService;

    @MockBean
    private MealMapper mealMapper;

    @Test
    @WithMockUser
    void getAllDiets_whenAuthenticated_returnsOkAndList() throws Exception {
        when(dietService.getAllDietsWithMeals()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/diets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(dietService).getAllDietsWithMeals();
    }

    @Test
    @WithMockUser
    void getAllDiets_whenHasData_returnsList() throws Exception {
        DietDto dto = new DietDto();
        dto.setId(1L);
        dto.setName("Test Diet");
        when(dietService.getAllDietsWithMeals()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/diets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Diet"));
    }

    @Test
    void getAllDiets_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/diets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
