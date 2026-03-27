package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.MealDto;
import com.myfitnessjourney.entity.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MealMapper {
    MealMapper INSTANCE = Mappers.getMapper(MealMapper.class);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    MealDto toDto(Meal meal);

    List<MealDto> toDtoList(List<Meal> meals);
}

