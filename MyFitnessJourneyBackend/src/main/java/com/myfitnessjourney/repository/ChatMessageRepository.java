package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.ChatMessage;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.repository.projection.PartnerLastMessageProjection;
import com.myfitnessjourney.repository.projection.PartnerUnreadCountProjection;
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

    @Query(value = """
            SELECT
                CASE WHEN sender_id = :userId THEN recipient_id ELSE sender_id END AS partner_id,
                MAX(sent_at) AS last_message_at
            FROM chat_messages
            WHERE sender_id = :userId OR recipient_id = :userId
            GROUP BY partner_id
            """, nativeQuery = true)
    List<PartnerLastMessageProjection> findLastMessageTimeByPartners(@Param("userId") Long userId);

    @Query(value = """
            SELECT sender_id AS partner_id, COUNT(*) AS unread_count
            FROM chat_messages
            WHERE recipient_id = :userId AND is_read = false
            GROUP BY sender_id
            """, nativeQuery = true)
    List<PartnerUnreadCountProjection> findUnreadCountByPartners(@Param("userId") Long userId);
    
    @Modifying
    @Query("""
            UPDATE ChatMessage m SET m.isRead = true
            WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false
            """)
    int markMessagesAsRead(@Param("recipient") User recipient, @Param("sender") User sender);

    long countByRecipientAndIsReadFalse(User recipient);
}
