import React, { useState, useEffect, useCallback, useRef } from 'react';
import { ChatWindow } from '../ChatWindow';
import { ChatMessageDto, ChatUserDto } from '../../../types/chat';
import { 
  getConversation, 
  sendMessage,
  markMessagesAsRead
} from '../../../services/chatService';
import './ChatMessages.css';

interface ChatMessagesProps {
  selectedUser: ChatUserDto | null;
  currentUserId: number;
  onBack: () => void;
  onNewMessageRef?: React.MutableRefObject<((message: ChatMessageDto) => void) | null>;
}

export const ChatMessages: React.FC<ChatMessagesProps> = ({ 
  selectedUser, 
  currentUserId,
  onBack,
  onNewMessageRef
}) => {
  const [messages, setMessages] = useState<ChatMessageDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const selectedUserRef = useRef<ChatUserDto | null>(null);

  useEffect(() => {
    selectedUserRef.current = selectedUser;
  }, [selectedUser]);

  const insertMessageInOrder = useCallback((newMessage: ChatMessageDto, currentMessages: ChatMessageDto[]): ChatMessageDto[] => {
    if (currentMessages.some((m) => m.id === newMessage.id)) {
      return currentMessages;
    }

    if (currentMessages.length === 0) {
      return [newMessage];
    }

    const newMessageTime = new Date(newMessage.sentAt).getTime();
    const insertIndex = currentMessages.findIndex(
      (m) => new Date(m.sentAt).getTime() > newMessageTime
    );

    if (insertIndex === -1) {
      return [...currentMessages, newMessage];
    }

    const newMessages = [...currentMessages];
    newMessages.splice(insertIndex, 0, newMessage);
    return newMessages;
  }, []);

  const handleNewMessage = useCallback((message: ChatMessageDto) => {
    const currentSelectedUser = selectedUserRef.current;
    if (currentSelectedUser && 
        (message.senderId === currentSelectedUser.id || message.recipientId === currentSelectedUser.id)) {
      setMessages((prev) => insertMessageInOrder(message, prev));
    }
  }, [insertMessageInOrder]);

  useEffect(() => {
    if (onNewMessageRef) {
      onNewMessageRef.current = handleNewMessage;
    }
  }, [onNewMessageRef, handleNewMessage]);

  useEffect(() => {
    const loadConversation = async () => {
      if (!selectedUser) {
        setMessages([]);
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        const conversation = await getConversation(selectedUser.id);
        setMessages(conversation);
        await markMessagesAsRead(selectedUser.id);
      } catch (error) {
        console.error('Failed to load conversation:', error);
        setError('Неуспешно зареждане на разговора');
      } finally {
        setIsLoading(false);
      }
    };

    loadConversation();
  }, [selectedUser]);

  const handleSendMessage = async (content: string) => {
    if (!selectedUser) return;

    setError(null);
    try {
      const message = await sendMessage({
        recipientId: selectedUser.id,
        content
      });
      
      setMessages((prev) => insertMessageInOrder(message, prev));
    } catch (error) {
      console.error('Failed to send message:', error);
      setError('Неуспешно изпращане на съобщението. Моля, опитайте отново.');
    }
  };

  return (
    <div className="chat-messages-container">
      {selectedUser && (
        <button className="chat-back-button" onClick={onBack}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
          Назад
        </button>
      )}
      {error && (
        <div className="chat-error-message">
          {error}
        </div>
      )}
      <ChatWindow
        selectedUser={selectedUser}
        messages={messages}
        currentUserId={currentUserId}
        onSendMessage={handleSendMessage}
        isLoading={isLoading}
      />
    </div>
  );
};
