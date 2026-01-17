package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.entity.Exercise;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseDto toDto(Exercise exercise);
    
    List<ExerciseDto> toDtoList(List<Exercise> exercises);
}
