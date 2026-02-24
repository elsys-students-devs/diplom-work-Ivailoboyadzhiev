package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.service.FitnessProgramService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FitnessProgramControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FitnessProgramService fitnessProgramService;

    @Test
    @WithMockUser
    void getAllFitnessPrograms_whenAuthenticated_returnsOkAndList() throws Exception {
        when(fitnessProgramService.getAllFitnessProgramsWithExercises()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/fitness-programs").with(user("user")).accept(MediaType.APPLICATION_JSON))
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

        mockMvc.perform(get("/api/fitness-programs").with(user("user")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Силова програма"));
    }

    @Test
    void getAllFitnessPrograms_whenNotAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/fitness-programs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    void getFitnessProgramById_whenFound_returnsOkAndDto() throws Exception {
        FitnessProgramDto dto = new FitnessProgramDto();
        dto.setId(1L);
        dto.setName("Кардио");
        when(fitnessProgramService.getFitnessProgramByIdWithExercises(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/fitness-programs/1").with(user("user")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Кардио"));
    }

    @Test
    @WithMockUser
    void getExercisesByFitnessProgramId_whenProgramExists_returnsOkAndList() throws Exception {
        when(fitnessProgramService.getExercisesByProgramId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/fitness-programs/1/exercises").with(user("user")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(fitnessProgramService).getExercisesByProgramId(1L);
    }

    @Test
    @WithMockUser
    void getExercisesByFitnessProgramId_whenProgramNotExists_returnsNotFound() throws Exception {
        when(fitnessProgramService.getExercisesByProgramId(999L))
                .thenThrow(new FitnessProgramNotFoundException("Fitness program not found with id: 999"));

        mockMvc.perform(get("/api/fitness-programs/999/exercises").with(user("user")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
