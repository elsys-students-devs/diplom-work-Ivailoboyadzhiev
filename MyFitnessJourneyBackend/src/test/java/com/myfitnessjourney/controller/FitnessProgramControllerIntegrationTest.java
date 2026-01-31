package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.service.ExerciseService;
import com.myfitnessjourney.service.FitnessProgramService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FitnessProgramController.class)
class FitnessProgramControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FitnessProgramService fitnessProgramService;

    @MockBean
    private ExerciseService exerciseService;

    @Test
    @WithMockUser
    void getAllFitnessPrograms_whenAuthenticated_returnsOkAndList() throws Exception {
        when(fitnessProgramService.getAllFitnessProgramsWithExercises()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/fitness-programs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(fitnessProgramService).getAllFitnessProgramsWithExercises();
    }

    @Test
    @WithMockUser
    void getAllFitnessPrograms_whenHasData_returnsList() throws Exception {
        FitnessProgramDto dto = new FitnessProgramDto();
        dto.setId(1L);
        dto.setName("Силова програма");
        when(fitnessProgramService.getAllFitnessProgramsWithExercises()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/fitness-programs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Силова програма"));
    }

    @Test
    void getAllFitnessPrograms_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/fitness-programs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getFitnessProgramById_whenFound_returnsOkAndDto() throws Exception {
        FitnessProgramDto dto = new FitnessProgramDto();
        dto.setId(1L);
        dto.setName("Кардио");
        when(fitnessProgramService.getFitnessProgramByIdWithExercises(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/fitness-programs/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Кардио"));
    }

    @Test
    @WithMockUser
    void getExercisesByFitnessProgramId_whenProgramExists_returnsOkAndList() throws Exception {
        when(fitnessProgramService.existsById(1L)).thenReturn(true);
        when(exerciseService.getExercisesByFitnessProgramId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/fitness-programs/1/exercises").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(exerciseService).getExercisesByFitnessProgramId(1L);
    }

    @Test
    @WithMockUser
    void getExercisesByFitnessProgramId_whenProgramNotExists_returnsNotFound() throws Exception {
        when(fitnessProgramService.existsById(999L)).thenReturn(false);

        mockMvc.perform(get("/api/fitness-programs/999/exercises").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
