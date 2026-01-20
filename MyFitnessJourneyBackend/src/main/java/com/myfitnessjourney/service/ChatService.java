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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        logger.info("Sending message from {} to user ID {}", senderEmail, request.getRecipientId());

        User sender = getUserByEmail(senderEmail);
        User recipient = getUserById(request.getRecipientId());

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        logger.info("Message sent successfully with ID: {}", savedMessage.getId());

        return chatMessageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversation(String currentUserEmail, Long otherUserId) {
        logger.info("Fetching conversation between {} and user ID {}", currentUserEmail, otherUserId);

        User currentUser = getUserByEmail(currentUserEmail);
        User otherUser = getUserById(otherUserId);

        List<ChatMessage> sentMessages = chatMessageRepository.findBySenderAndRecipientOrderBySentAtAsc(currentUser, otherUser);
        List<ChatMessage> receivedMessages = chatMessageRepository.findBySenderAndRecipientOrderBySentAtAsc(otherUser, currentUser);

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);
        allMessages.sort(Comparator.comparing(ChatMessage::getSentAt));

        return chatMessageMapper.toDtoList(allMessages);
    }

    @Transactional
    public void markMessagesAsRead(String recipientEmail, Long senderId) {
        logger.info("Marking messages as read from user ID {} to {}", senderId, recipientEmail);

        User recipient = getUserByEmail(recipientEmail);
        User sender = getUserById(senderId);

        List<ChatMessage> unreadMessages = chatMessageRepository.findByRecipientAndSenderAndIsReadFalse(recipient, sender);
        unreadMessages.forEach(msg -> msg.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);

        logger.info("Marked {} messages as read", unreadMessages.size());
    }

    @Transactional(readOnly = true)
    public List<ChatUserDto> getChatPartners(String userEmail) {
        logger.info("Fetching chat partners for {}", userEmail);

        User user = getUserByEmail(userEmail);

        List<ChatMessage> allUserMessages = chatMessageRepository.findBySenderOrRecipientOrderBySentAtAsc(user, user);

        Set<User> partners = new HashSet<>();
        for (ChatMessage message : allUserMessages) {
            if (message.getSender().equals(user)) {
                partners.add(message.getRecipient());
            } else {
                partners.add(message.getSender());
            }
        }

        return chatUserMapper.toDtoList(new ArrayList<>(partners));
    }

    @Transactional(readOnly = true)
    public List<ChatUserDto> searchUsers(String query, String currentUserEmail) {
        logger.info("Searching users with query: {} for user: {}", query, currentUserEmail);

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
