package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.ChatMessageDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ChatNotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUsers(ChatMessageDto message) {
        logger.info("Sending WebSocket notification for message ID: {}", message.getId());
        
        // Send to recipient's topic
        String recipientDestination = "/topic/chat/" + message.getRecipientId();
        messagingTemplate.convertAndSend(recipientDestination, message);
        
        logger.info("Message delivered via WebSocket to topic: {}", recipientDestination);
    }
}
