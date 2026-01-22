package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.service.ChatService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid @Payload SendMessageRequest request, Principal principal) {
        if (principal == null || principal.getName() == null) {
            logger.warn("Unauthenticated WebSocket message received");
            return;
        }
        
        logger.debug("WebSocket message received from {}", principal.getName());
        
        ChatMessageDto message = chatService.sendMessage(principal.getName(), request);
        
        // Send to recipient's private queue using username
        messagingTemplate.convertAndSendToUser(
                message.getRecipientUsername(),
                "/queue/messages",
                message
        );
        
        logger.debug("Message delivered via WebSocket to user {}", message.getRecipientUsername());
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Valid @Payload Long chatPartnerId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            logger.warn("Unauthenticated WebSocket markAsRead received");
            return;
        }
        
        logger.debug("Marking messages from {} as read for {}", chatPartnerId, principal.getName());
        chatService.markMessagesAsRead(principal.getName(), chatPartnerId);
    }
}
