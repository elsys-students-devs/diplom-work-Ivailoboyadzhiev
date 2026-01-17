package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.entity.FitnessProgram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ExerciseMapper.class)
public interface FitnessProgramMapper {
    
    @Mapping(target = "exercises", source = "exercises")
    FitnessProgramDto toDto(FitnessProgram fitnessProgram);

    List<FitnessProgramDto> toDtoList(List<FitnessProgram> fitnessPrograms);
}
