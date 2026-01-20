package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.service.ChatService;
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
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        logger.info("WebSocket message received from {}", principal.getName());
        
        ChatMessageDto message = chatService.sendMessage(principal.getName(), request);
        
        // Send to recipient's private queue
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId().toString(),
                "/queue/messages",
                message
        );
        
        // Send confirmation back to sender
        messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(),
                "/queue/messages",
                message
        );
        
        logger.info("Message delivered via WebSocket to users {} and {}", 
                message.getSenderId(), message.getRecipientId());
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Long senderId, Principal principal) {
        logger.info("Marking messages from {} as read for {}", senderId, principal.getName());
        chatService.markMessagesAsRead(principal.getName(), senderId);
    }
}
