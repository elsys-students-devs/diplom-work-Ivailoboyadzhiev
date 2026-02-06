package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ExerciseMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "muscleGroup", ignore = true)
    ExerciseDto toDto(Exercise exercise);

    List<ExerciseDto> toDtoList(List<Exercise> exercises);
}
