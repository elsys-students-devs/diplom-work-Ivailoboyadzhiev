package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.entity.ChatMessage;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.exception.UserNotFoundException;
import com.myfitnessjourney.mapper.ChatMessageMapper;
import com.myfitnessjourney.mapper.ChatUserMapper;
import com.myfitnessjourney.repository.ChatMessageRepository;
import com.myfitnessjourney.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatUserMapper chatUserMapper;

    @Transactional
    public ChatMessageDto sendMessage(String senderEmail, SendMessageRequest request) {
        logger.debug("Sending message from {} to user ID {}", senderEmail, request.getRecipientId());

        User sender = getUserByEmail(senderEmail);
        User recipient = getUserById(request.getRecipientId());

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        logger.debug("Message sent successfully with ID: {}", savedMessage.getId());

        return chatMessageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversation(String currentUserEmail, Long chatPartnerId) {
        logger.debug("Fetching conversation between {} and user ID {}", currentUserEmail, chatPartnerId);

        User currentUser = getUserByEmail(currentUserEmail);
        User chatPartner = getUserById(chatPartnerId);

        List<ChatMessage> messages = chatMessageRepository.findConversation(currentUser, chatPartner);

        return chatMessageMapper.toDtoList(messages);
    }

    @Transactional
    public void markMessagesAsRead(String recipientEmail, Long chatPartnerId) {
        logger.debug("Marking messages as read from user ID {} to {}", chatPartnerId, recipientEmail);

        User recipient = getUserByEmail(recipientEmail);
        User sender = getUserById(chatPartnerId);

        int updatedCount = chatMessageRepository.markMessagesAsRead(recipient, sender);

        logger.debug("Marked {} messages as read", updatedCount);
    }

    @Transactional(readOnly = true)
    public List<ChatUserDto> getChatPartners(String userEmail) {
        logger.debug("Fetching chat partners for {}", userEmail);

        User user = getUserByEmail(userEmail);

        List<User> partners = chatMessageRepository.findChatPartners(user);

        return chatUserMapper.toDtoList(partners);
    }

    @Transactional(readOnly = true)
    public List<ChatUserDto> searchUsers(String query, String currentUserEmail) {
        logger.debug("Searching users with query: {} for user: {}", query, currentUserEmail);

        User currentUser = getUserByEmail(currentUserEmail);

        List<User> matchedUsers = userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        return chatUserMapper.toDtoList(matchedUsers);
    }

    @Transactional(readOnly = true)
    public long getUnreadMessageCount(String userEmail) {
        User user = getUserByEmail(userEmail);
        return chatMessageRepository.countByRecipientAndIsReadFalse(user);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }
}
