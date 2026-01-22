import React, { useState, useEffect, useCallback, useRef } from 'react';
import { UserList } from '../UserList';
import { ChatWindow } from '../ChatWindow';
import { ChatMessageDto, ChatUserDto } from '../../../types/chat';
import { 
  getConversation, 
  getChatPartners, 
  searchUsers, 
  sendMessage,
  markMessagesAsRead,
  connectWebSocket,
  disconnectWebSocket
} from '../../../services/chatService';
import './ChatMain.css';

interface ChatMainProps {
  currentUserId: number;
}

export const ChatMain: React.FC<ChatMainProps> = ({ currentUserId }) => {
  const [users, setUsers] = useState<ChatUserDto[]>([]);
  const [selectedUser, setSelectedUser] = useState<ChatUserDto | null>(null);
  const [messages, setMessages] = useState<ChatMessageDto[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [showUserList, setShowUserList] = useState(true);

  // Use ref to access current selectedUser in WebSocket callback
  const selectedUserRef = useRef<ChatUserDto | null>(null);
  
  useEffect(() => {
    selectedUserRef.current = selectedUser;
  }, [selectedUser]);

  const handleNewMessage = useCallback((message: ChatMessageDto) => {
    const currentSelectedUser = selectedUserRef.current;
    
    // Add message if it's from/to the currently selected user
    if (currentSelectedUser && 
        (message.senderId === currentSelectedUser.id || message.recipientId === currentSelectedUser.id)) {
      setMessages((prev) => {
        const exists = prev.some((m) => m.id === message.id);
        if (exists) return prev;
        // Sort messages by sentAt to handle out-of-order delivery
        return [...prev, message].sort((a, b) => 
          new Date(a.sentAt).getTime() - new Date(b.sentAt).getTime()
        );
      });
    }
  }, []);

  useEffect(() => {
    connectWebSocket(currentUserId, handleNewMessage);
    
    return () => {
      disconnectWebSocket();
    };
  }, [currentUserId, handleNewMessage]);

  useEffect(() => {
    const loadUsers = async () => {
      setIsLoadingUsers(true);
      try {
        if (searchQuery.trim()) {
          const results = await searchUsers(searchQuery);
          setUsers(results);
        } else {
          const partners = await getChatPartners();
          setUsers(partners);
        }
      } catch (error) {
        console.error('Failed to load users:', error);
      } finally {
        setIsLoadingUsers(false);
      }
    };

    const debounceTimer = setTimeout(loadUsers, 300);
    return () => clearTimeout(debounceTimer);
  }, [searchQuery]);

  useEffect(() => {
    const loadConversation = async () => {
      if (!selectedUser) {
        setMessages([]);
        return;
      }

      setIsLoadingMessages(true);
      try {
        const conversation = await getConversation(selectedUser.id);
        setMessages(conversation);
        await markMessagesAsRead(selectedUser.id);
      } catch (error) {
        console.error('Failed to load conversation:', error);
      } finally {
        setIsLoadingMessages(false);
      }
    };

    loadConversation();
  }, [selectedUser]);

  const handleSelectUser = (user: ChatUserDto) => {
    setSelectedUser(user);
    setShowUserList(false);
  };

  const handleSendMessage = async (content: string) => {
    if (!selectedUser) return;

    try {
      const message = await sendMessage({
        recipientId: selectedUser.id,
        content
      });
      
      setMessages((prev) => {
        const exists = prev.some((m) => m.id === message.id);
        if (exists) return prev;
        return [...prev, message].sort((a, b) => 
          new Date(a.sentAt).getTime() - new Date(b.sentAt).getTime()
        );
      });
    } catch (error) {
      console.error('Failed to send message:', error);
    }
  };

  const handleBackToList = () => {
    setShowUserList(true);
    setSelectedUser(null);
  };

  return (
    <div className="chat-main-container">
      <div className={`chat-sidebar ${!showUserList ? 'hidden-mobile' : ''}`}>
        <UserList
          users={users}
          selectedUserId={selectedUser?.id || null}
          onSelectUser={handleSelectUser}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          isLoading={isLoadingUsers}
        />
      </div>
      <div className={`chat-content ${showUserList ? 'hidden-mobile' : ''}`}>
        {selectedUser && (
          <button className="chat-back-button" onClick={handleBackToList}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="15 18 9 12 15 6"></polyline>
            </svg>
            Назад
          </button>
        )}
        <ChatWindow
          selectedUser={selectedUser}
          messages={messages}
          currentUserId={currentUserId}
          onSendMessage={handleSendMessage}
          isLoading={isLoadingMessages}
        />
      </div>
    </div>
  );
};
