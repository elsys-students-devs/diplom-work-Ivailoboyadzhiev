package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.ChatMessage;
import com.myfitnessjourney.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE (m.sender = :user1 AND m.recipient = :user2)
            OR (m.sender = :user2 AND m.recipient = :user1)
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT DISTINCT m.recipient FROM ChatMessage m WHERE m.sender = :user")
    List<User> findRecipientsBySender(@Param("user") User user);
    
    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.recipient = :user")
    List<User> findSendersByRecipient(@Param("user") User user);
    
    @Query("""
            SELECT MAX(m.sentAt) FROM ChatMessage m
            WHERE (m.sender = :user1 AND m.recipient = :user2)
            OR (m.sender = :user2 AND m.recipient = :user1)
            """)
    java.time.LocalDateTime findLastMessageTime(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("""
            SELECT COUNT(m) FROM ChatMessage m
            WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false
            """)
    long countUnreadMessages(@Param("recipient") User recipient, @Param("sender") User sender);

    @Modifying
    @Query("""
            UPDATE ChatMessage m SET m.isRead = true
            WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false
            """)
    int markMessagesAsRead(@Param("recipient") User recipient, @Param("sender") User sender);

    long countByRecipientAndIsReadFalse(User recipient);
}
