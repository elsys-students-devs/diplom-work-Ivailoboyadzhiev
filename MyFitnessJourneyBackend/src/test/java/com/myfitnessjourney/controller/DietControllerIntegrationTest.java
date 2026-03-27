package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.service.DietService;
import com.myfitnessjourney.service.MealService;
import com.myfitnessjourney.mapper.MealMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DietControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DietService dietService;

    @MockitoBean
    private MealService mealService;

    @MockitoBean
    private MealMapper mealMapper;

    @Test
    @WithMockUser
    void getAllDiets_whenAuthenticated_returnsOkAndList() throws Exception {
        when(dietService.getAllDietsWithMeals()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/diets").with(user("user")).accept(MediaType.APPLICATION_JSON))
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

        mockMvc.perform(get("/api/diets").with(user("user")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Diet"));
    }

    @Test
    void getAllDiets_whenNotAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/diets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}
