package com.myfitnessjourney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDto {
    private Long id;
    private String username;
    private String name;
    private String pictureUrl;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
}
