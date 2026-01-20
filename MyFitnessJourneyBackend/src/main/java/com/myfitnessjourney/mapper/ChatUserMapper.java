package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {

    ChatUserDto toDto(User user);

    List<ChatUserDto> toDtoList(List<User> users);
}
