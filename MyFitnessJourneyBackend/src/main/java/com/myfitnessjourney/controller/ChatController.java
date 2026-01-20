package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.service.ChatNotificationService;
import com.myfitnessjourney.service.ChatService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatNotificationService chatNotificationService;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        ChatMessageDto message = chatService.sendMessage(userDetails.getUsername(), request);
        chatNotificationService.notifyUsers(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<ChatMessageDto>> getConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        List<ChatMessageDto> messages = chatService.getConversation(userDetails.getUsername(), userId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/read/{senderId}")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long senderId) {
        chatService.markMessagesAsRead(userDetails.getUsername(), senderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/partners")
    public ResponseEntity<List<ChatUserDto>> getChatPartners(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ChatUserDto> partners = chatService.getChatPartners(userDetails.getUsername());
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<ChatUserDto>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String query) {
        List<ChatUserDto> users = chatService.searchUsers(query, userDetails.getUsername());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = chatService.getUnreadMessageCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
