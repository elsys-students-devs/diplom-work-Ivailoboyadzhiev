package com.myfitnessjourney.mapper;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "recipientUsername", source = "recipient.username")
    @Mapping(target = "read", source = "read")
    ChatMessageDto toDto(ChatMessage message);

    List<ChatMessageDto> toDtoList(List<ChatMessage> messages);
}
