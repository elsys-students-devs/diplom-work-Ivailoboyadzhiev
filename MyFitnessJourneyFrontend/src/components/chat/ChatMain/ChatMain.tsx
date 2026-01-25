import React, { useState, useEffect, useCallback, useRef } from 'react';
import { ChatUserDto, ChatMessageDto } from '../../../types/chat';
import { ChatUsers } from '../ChatUsers';
import { ChatMessages } from '../ChatMessages';
import { connectWebSocket, disconnectWebSocket } from '../../../services/chatService';
import './ChatMain.css';

interface ChatMainProps {
  currentUserId: number;
}

export const ChatMain: React.FC<ChatMainProps> = ({ currentUserId }) => {
  const [selectedUser, setSelectedUser] = useState<ChatUserDto | null>(null);
  const [showUserList, setShowUserList] = useState(true);
  const refreshUsersRef = useRef<(() => void) | null>(null);
  const chatMessagesHandlerRef = useRef<((message: ChatMessageDto) => void) | null>(null);

  useEffect(() => {
    const wsHandler = (message: ChatMessageDto) => {
      if (chatMessagesHandlerRef.current) {
        chatMessagesHandlerRef.current(message);
      }
      setTimeout(() => {
        if (refreshUsersRef.current) {
          refreshUsersRef.current();
        }
      }, 100);
    };
    
    connectWebSocket(currentUserId, wsHandler);
    
    return () => {
      disconnectWebSocket();
    };
  }, [currentUserId]);

  const handleSelectUser = (user: ChatUserDto) => {
    setSelectedUser(user);
    setShowUserList(false);
  };

  const handleBackToList = () => {
    setShowUserList(true);
    setSelectedUser(null);
  };

  return (
    <div className="chat-main-container">
      <div className={`chat-sidebar ${!showUserList ? 'hidden-mobile' : ''}`}>
        <ChatUsers
          selectedUserId={selectedUser?.id || null}
          onSelectUser={handleSelectUser}
          onRefreshRef={refreshUsersRef}
        />
      </div>
      <div className={`chat-content ${showUserList ? 'hidden-mobile' : ''}`}>
        <ChatMessages
          selectedUser={selectedUser}
          currentUserId={currentUserId}
          onBack={handleBackToList}
          onNewMessageRef={chatMessagesHandlerRef}
        />
      </div>
    </div>
  );
};
