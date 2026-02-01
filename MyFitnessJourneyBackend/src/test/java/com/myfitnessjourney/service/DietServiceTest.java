package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.mapper.DietMapper;
import com.myfitnessjourney.repository.DietRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock
    private DietRepository dietRepository;

    @Mock
    private DietMapper dietMapper;

    @InjectMocks
    private DietService dietService;

    @Test
    void getAllDietsWithMeals_whenEmpty_returnsEmptyList() {
        when(dietRepository.findAllWithMeals()).thenReturn(Collections.emptyList());
        when(dietMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<DietDto> result = dietService.getAllDietsWithMeals();

        assertThat(result).isEmpty();
        verify(dietRepository).findAllWithMeals();
    }

    @Test
    void getAllDietsWithMeals_whenHasDiets_returnsMappedList() {
        Diet diet = new Diet();
        diet.setId(1L);
        diet.setName("Test Diet");
        List<Diet> diets = List.of(diet);
        DietDto dto = new DietDto();
        dto.setId(1L);
        dto.setName("Test Diet");
        List<DietDto> dtos = List.of(dto);

        when(dietRepository.findAllWithMeals()).thenReturn(diets);
        when(dietMapper.toDtoList(diets)).thenReturn(dtos);

        List<DietDto> result = dietService.getAllDietsWithMeals();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Diet");
        verify(dietRepository).findAllWithMeals();
    }

    @Test
    void getDietByIdWithMeals_whenNotFound_returnsEmpty() {
        when(dietRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<DietDto> result = dietService.getDietByIdWithMeals(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void getDietByIdWithMeals_whenFound_returnsMappedDto() {
        Diet diet = new Diet();
        diet.setId(1L);
        diet.setName("Test");
        DietDto dto = new DietDto();
        dto.setId(1L);
        dto.setName("Test");

        when(dietRepository.findById(1L)).thenReturn(Optional.of(diet));
        when(dietMapper.toDto(diet)).thenReturn(dto);

        Optional<DietDto> result = dietService.getDietByIdWithMeals(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test");
    }
}
