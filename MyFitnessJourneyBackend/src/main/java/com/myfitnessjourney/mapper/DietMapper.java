package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.entity.Diet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface DietMapper {

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "benefits", ignore = true)
    @Mapping(target = "meals", source = "meals")
    DietDto toDto(Diet diet);

    List<DietDto> toDtoList(List<Diet> diets);
}

