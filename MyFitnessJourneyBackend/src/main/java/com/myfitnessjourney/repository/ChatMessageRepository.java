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

    @Query("SELECT m FROM ChatMessage m " +
           "WHERE (m.sender = :user1 AND m.recipient = :user2) " +
           "OR (m.sender = :user2 AND m.recipient = :user1) " +
           "ORDER BY m.sentAt ASC")
    List<ChatMessage> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT DISTINCT CASE WHEN m.sender = :user THEN m.recipient ELSE m.sender END " +
           "FROM ChatMessage m " +
           "WHERE m.sender = :user OR m.recipient = :user")
    List<User> findChatPartners(@Param("user") User user);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true " +
           "WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false")
    int markMessagesAsRead(@Param("recipient") User recipient, @Param("sender") User sender);

    long countByRecipientAndIsReadFalse(User recipient);
}
