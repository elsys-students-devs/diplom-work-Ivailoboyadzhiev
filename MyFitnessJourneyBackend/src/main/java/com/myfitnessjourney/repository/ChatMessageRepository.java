package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.ChatMessage;
import com.myfitnessjourney.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderAndRecipientOrderBySentAtAsc(User sender, User recipient);

    List<ChatMessage> findBySenderOrRecipientOrderBySentAtAsc(User sender, User recipient);

    List<ChatMessage> findByRecipientAndIsReadFalse(User recipient);

    List<ChatMessage> findByRecipientAndSenderAndIsReadFalse(User recipient, User sender);

    long countByRecipientAndIsReadFalse(User recipient);
}
