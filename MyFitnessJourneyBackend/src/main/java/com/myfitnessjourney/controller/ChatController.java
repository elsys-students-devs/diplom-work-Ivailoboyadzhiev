package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.dto.UnreadCountDto;
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

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatNotificationService chatNotificationService;

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        ChatMessageDto message = chatService.sendMessage(userDetails.getUsername(), request);
        chatNotificationService.notifyUsers(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversations/{chatPartnerId}")
    public ResponseEntity<List<ChatMessageDto>> getConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatPartnerId) {
        List<ChatMessageDto> messages = chatService.getConversation(userDetails.getUsername(), chatPartnerId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/messages/read/{chatPartnerId}")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatPartnerId) {
        chatService.markMessagesAsRead(userDetails.getUsername(), chatPartnerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partners")
    public ResponseEntity<List<ChatUserDto>> getChatPartners(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ChatUserDto> partners = chatService.getChatPartners(userDetails.getUsername());
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ChatUserDto>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String query) {
        List<ChatUserDto> users = chatService.searchUsers(query, userDetails.getUsername());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/messages/unread/count")
    public ResponseEntity<UnreadCountDto> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = chatService.getUnreadMessageCount(userDetails.getUsername());
        return ResponseEntity.ok(new UnreadCountDto(count));
    }
}
