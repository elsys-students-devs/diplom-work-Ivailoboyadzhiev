package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.entity.FitnessProgram;
import com.myfitnessjourney.entity.FitnessProgramTranslation;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.mapper.FitnessProgramMapper;
import com.myfitnessjourney.repository.FitnessProgramRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FitnessProgramServiceTest {

    @Mock
    private FitnessProgramRepository fitnessProgramRepository;

    @Mock
    private FitnessProgramMapper fitnessProgramMapper;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private FitnessProgramService fitnessProgramService;

    @Test
    void getAllFitnessProgramsWithExercises_whenEmpty_returnsEmptyList() {
        when(fitnessProgramRepository.findAllWithExercises()).thenReturn(Collections.emptyList());
        when(fitnessProgramMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<FitnessProgramDto> result = fitnessProgramService.getAllFitnessProgramsWithExercises();

        assertThat(result).isEmpty();
        verify(fitnessProgramRepository).findAllWithExercises();
    }

    @Test
    void getAllFitnessProgramsWithExercises_whenHasPrograms_returnsMappedList() {
        FitnessProgram program = new FitnessProgram();
        program.setId(1L);
        FitnessProgramTranslation tr = new FitnessProgramTranslation();
        tr.setName("Силова");
        tr.setDescription("desc");
        tr.setLocale("bg");
        program.setTranslations(List.of(tr));
        List<FitnessProgram> programs = List.of(program);
        FitnessProgramDto dto = new FitnessProgramDto();
        dto.setId(1L);
        dto.setName("Силова");
        List<FitnessProgramDto> dtos = List.of(dto);

        when(fitnessProgramRepository.findAllWithExercises()).thenReturn(programs);
        when(fitnessProgramMapper.toDtoList(programs)).thenReturn(dtos);

        List<FitnessProgramDto> result = fitnessProgramService.getAllFitnessProgramsWithExercises();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Силова");
    }

    @Test
    void getFitnessProgramByIdWithExercises_whenNotFound_throws() {
        when(fitnessProgramRepository.findByIdWithExercises(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fitnessProgramService.getFitnessProgramByIdWithExercises(999L))
                .isInstanceOf(FitnessProgramNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getFitnessProgramByIdWithExercises_whenFound_returnsDto() {
        FitnessProgram program = new FitnessProgram();
        program.setId(1L);
        FitnessProgramTranslation tr = new FitnessProgramTranslation();
        tr.setName("Кардио");
        tr.setDescription("desc");
        tr.setLocale("bg");
        program.setTranslations(List.of(tr));
        FitnessProgramDto dto = new FitnessProgramDto();
        dto.setId(1L);
        dto.setName("Кардио");

        when(fitnessProgramRepository.findByIdWithExercises(1L)).thenReturn(Optional.of(program));
        when(fitnessProgramMapper.toDto(program)).thenReturn(dto);

        FitnessProgramDto result = fitnessProgramService.getFitnessProgramByIdWithExercises(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Кардио");
    }

    @Test
    void existsById_whenExists_returnsTrue() {
        when(fitnessProgramRepository.existsById(1L)).thenReturn(true);

        boolean result = fitnessProgramService.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_whenNotExists_returnsFalse() {
        when(fitnessProgramRepository.existsById(999L)).thenReturn(false);

        boolean result = fitnessProgramService.existsById(999L);

        assertThat(result).isFalse();
    }

    @Test
    void getExercisesByProgramId_whenProgramExists_returnsExercisesFromExerciseService() {
        when(fitnessProgramRepository.existsById(1L)).thenReturn(true);
        when(exerciseService.getExercisesByFitnessProgramId(1L)).thenReturn(Collections.emptyList());

        List<ExerciseDto> result = fitnessProgramService.getExercisesByProgramId(1L);

        assertThat(result).isEmpty();
        verify(exerciseService).getExercisesByFitnessProgramId(1L);
    }

    @Test
    void getExercisesByProgramId_whenProgramNotExists_throws() {
        when(fitnessProgramRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> fitnessProgramService.getExercisesByProgramId(999L))
                .isInstanceOf(FitnessProgramNotFoundException.class)
                .hasMessageContaining("999");
    }
}
