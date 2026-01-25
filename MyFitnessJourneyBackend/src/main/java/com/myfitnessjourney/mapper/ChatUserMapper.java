package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {

    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    ChatUserDto toDto(User user);

    List<ChatUserDto> toDtoList(List<User> users);
}
