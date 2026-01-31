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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final String AI_USER_EMAIL = "groc@myfitnessjourney.ai";

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatUserMapper chatUserMapper;
    private final GroqAiService groqAiService;

    @Transactional
    public ChatMessageDto sendMessage(String senderEmail, SendMessageRequest request) {
        logger.debug("Sending message from {} to user ID {}", senderEmail, request.getRecipientId());

        User sender = getUserByEmail(senderEmail);
        User recipient = getUserById(request.getRecipientId());

        // Save user's message
        ChatMessage userMessage = ChatMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .build();

        ChatMessage savedUserMessage = chatMessageRepository.save(userMessage);
        logger.debug("User message sent successfully with ID: {}", savedUserMessage.getId());

        // If recipient is AI user, generate and save AI response
        logger.debug("Checking if recipient {} is AI user {}", recipient.getEmail(), AI_USER_EMAIL);
        if (AI_USER_EMAIL.equals(recipient.getEmail())) {
            logger.info("AI message received from user {} (ID: {}) to AI user {} (ID: {}): {}", 
                sender.getEmail(), sender.getId(), recipient.getEmail(), recipient.getId(), request.getContent());
            try {
                // Get conversation history for context
                String conversationHistory = buildConversationHistory(sender, recipient);
                logger.debug("Conversation history length: {}", conversationHistory.length());
                
                // Generate AI response
                logger.info("Calling Groq AI service...");
                String aiResponse = groqAiService.generateResponse(request.getContent(), conversationHistory);
                logger.info("AI response generated: {}", aiResponse.substring(0, Math.min(100, aiResponse.length())));
                
                // Save AI response
                ChatMessage aiMessage = ChatMessage.builder()
                        .sender(recipient) // AI is the sender
                        .recipient(sender) // User is the recipient
                        .content(aiResponse)
                        .isRead(false)
                        .build();
                
                ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);
                logger.info("AI response saved successfully with ID: {}", savedAiMessage.getId());
            } catch (Exception e) {
                logger.error("Error generating AI response for user {}: {}", sender.getEmail(), e.getMessage(), e);
                // Save error message to user
                try {
                    ChatMessage errorMessage = ChatMessage.builder()
                            .sender(recipient)
                            .recipient(sender)
                            .content("Извинявам се, възникна техническа грешка. Моля опитайте отново по-късно.")
                            .isRead(false)
                            .build();
                    chatMessageRepository.save(errorMessage);
                    logger.info("Error message saved to user");
                } catch (Exception saveError) {
                    logger.error("Failed to save error message", saveError);
                }
            }
        }

        return chatMessageMapper.toDto(savedUserMessage);
    }

    private String buildConversationHistory(User user, User aiUser) {
        List<ChatMessage> recentMessages = chatMessageRepository.findConversation(user, aiUser);
        if (recentMessages.size() > 10) {
            recentMessages = recentMessages.subList(recentMessages.size() - 10, recentMessages.size());
        }
        
        StringBuilder history = new StringBuilder();
        for (ChatMessage msg : recentMessages) {
            String role = msg.getSender().getId().equals(aiUser.getId()) ? "Assistant" : "User";
            history.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        return history.toString();
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
    public List<ChatUserDto> searchUsers(String query, String currentUserEmail) {
        logger.debug("Searching users with query: {} for user: {}", query, currentUserEmail);

        User currentUser = getUserByEmail(currentUserEmail);

        List<User> matchedUsers = userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        // Add AI user if query matches "groc" or "ai"
        if (query != null && (query.toLowerCase().contains("groc") || query.toLowerCase().contains("ai") || query.toLowerCase().contains("асистент"))) {
            User aiUser = getOrCreateAiUser();
            if (!matchedUsers.stream().anyMatch(u -> u.getId().equals(aiUser.getId()))) {
                matchedUsers.add(aiUser);
            }
        }

        return chatUserMapper.toDtoList(matchedUsers);
    }

    @Transactional(readOnly = true)
    public List<ChatUserDto> getChatPartners(String userEmail) {
        logger.debug("Fetching chat partners for {}", userEmail);

        User user = getUserByEmail(userEmail);

        List<User> recipients = chatMessageRepository.findRecipientsBySender(user);
        List<User> senders = chatMessageRepository.findSendersByRecipient(user);
        
        Set<User> partnersSet = new HashSet<>(recipients);
        partnersSet.addAll(senders);
        
        // Always include AI user
        User aiUser = getOrCreateAiUser();
        partnersSet.add(aiUser);
        
        List<User> partners = new ArrayList<>(partnersSet);

        List<ChatUserDto> partnerDtos = partners.stream()
                .map(partner -> {
                    ChatUserDto dto = chatUserMapper.toDto(partner);
                    java.time.LocalDateTime lastMessageAt = chatMessageRepository.findLastMessageTime(user, partner);
                    long unreadCount = chatMessageRepository.countUnreadMessages(user, partner);
                    dto.setLastMessageAt(lastMessageAt);
                    dto.setUnreadCount(unreadCount);
                    return dto;
                })
                .sorted((dto1, dto2) -> {
                    if (dto1.getLastMessageAt() == null && dto2.getLastMessageAt() == null) {
                        return 0;
                    }
                    if (dto1.getLastMessageAt() == null) {
                        return 1;
                    }
                    if (dto2.getLastMessageAt() == null) {
                        return -1;
                    }
                    return dto2.getLastMessageAt().compareTo(dto1.getLastMessageAt());
                })
                .collect(Collectors.toList());

        return partnerDtos;
    }

    @Transactional
    public User getOrCreateAiUser() {
        logger.debug("Getting or creating AI user with email: {}", AI_USER_EMAIL);
        User aiUser = userRepository.findByEmail(AI_USER_EMAIL)
                .orElseGet(() -> {
                    logger.info("AI user not found, creating new AI user...");
                    User newAiUser = new User();
                    newAiUser.setEmail(AI_USER_EMAIL);
                    newAiUser.setUsername("groc");
                    newAiUser.setName("Groc - AI Fitness Assistant");
                    newAiUser.setPassword(null); // No password for AI user
                    User saved = userRepository.save(newAiUser);
                    logger.info("AI user created successfully with ID: {}", saved.getId());
                    return saved;
                });
        logger.debug("AI user found/created with ID: {}", aiUser.getId());
        return aiUser;
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
