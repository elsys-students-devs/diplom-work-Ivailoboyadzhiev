import React, { useState, useRef, useEffect } from 'react';
import { ChatMessageDto, ChatUserDto } from '../../../types/chat';
import './ChatWindow.css';

interface ChatWindowProps {
  selectedUser: ChatUserDto | null;
  messages: ChatMessageDto[];
  currentUserId: number;
  onSendMessage: (content: string) => void;
  isLoading: boolean;
}

export const ChatWindow: React.FC<ChatWindowProps> = ({
  selectedUser,
  messages,
  currentUserId,
  onSendMessage,
  isLoading
}) => {
  const [messageInput, setMessageInput] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (selectedUser && inputRef.current) {
      inputRef.current.focus();
    }
  }, [selectedUser]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (messageInput.trim() && selectedUser) {
      onSendMessage(messageInput.trim());
      setMessageInput('');
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const parseDateTime = (dateString: string): Date => {
    // Backend returns LocalDateTime in UTC without timezone suffix
    // We treat it as UTC and let the browser convert to user's local timezone
    // If it already has Z or timezone offset, parse directly
    if (dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString)) {
      return new Date(dateString);
    }
    // Remove nanoseconds/milliseconds if present and add Z to indicate UTC
    const cleanedDateString = dateString.split('.')[0] + 'Z';
    return new Date(cleanedDateString);
  };

  const formatTime = (dateString: string): string => {
    const date = parseDateTime(dateString);
    return date.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
  };

  const formatDate = (dateString: string): string => {
    const date = parseDateTime(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return date.toLocaleDateString(undefined, { day: 'numeric', month: 'long', year: 'numeric' });
    }
  };

  const getDisplayName = (user: ChatUserDto): string => {
    return user.username || user.name || 'Потребител';
  };

  const getInitial = (user: ChatUserDto): string => {
    const name = getDisplayName(user);
    return name.charAt(0).toUpperCase();
  };

  const groupMessagesByDate = (messages: ChatMessageDto[]): Map<string, ChatMessageDto[]> => {
    const groups = new Map<string, ChatMessageDto[]>();
    messages.forEach((message) => {
      const dateKey = parseDateTime(message.sentAt).toDateString();
      if (!groups.has(dateKey)) {
        groups.set(dateKey, []);
      }
      groups.get(dateKey)!.push(message);
    });
    return groups;
  };

  if (!selectedUser) {
    return (
      <div className="chat-window-container">
        <div className="chat-window-empty">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#ccc" strokeWidth="1">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          <h3>Изберете разговор</h3>
          <p>Изберете потребител от списъка, за да започнете чат</p>
        </div>
      </div>
    );
  }

  const messageGroups = groupMessagesByDate(messages);

  return (
    <div className="chat-window-container">
      <div className="chat-window-header">
        <div className="chat-user-avatar">
          {selectedUser.pictureUrl ? (
            <img src={selectedUser.pictureUrl} alt={getDisplayName(selectedUser)} />
          ) : (
            <span>{getInitial(selectedUser)}</span>
          )}
        </div>
        <div className="chat-user-info">
          <span className="chat-user-name">{getDisplayName(selectedUser)}</span>
        </div>
      </div>

      <div className="chat-messages-container">
        {isLoading ? (
          <div className="chat-loading">
            <div className="loading-spinner"></div>
            <span>Зареждане на съобщения...</span>
          </div>
        ) : messages.length === 0 ? (
          <div className="chat-no-messages">
            <p>Няма съобщения. Започнете разговор!</p>
          </div>
        ) : (
          Array.from(messageGroups.entries()).map(([dateKey, dateMessages]) => (
            <div key={dateKey} className="message-date-group">
              <div className="message-date-divider">
                <span>{formatDate(dateMessages[0].sentAt)}</span>
              </div>
              {dateMessages.map((message) => (
                <div
                  key={message.id}
                  className={`message ${message.senderId === currentUserId ? 'sent' : 'received'}`}
                >
                  <div className="message-content">
                    <p>{message.content}</p>
                    <span className="message-time">{formatTime(message.sentAt)}</span>
                  </div>
                </div>
              ))}
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>

      <form className="chat-input-container" onSubmit={handleSubmit}>
        <input
          ref={inputRef}
          type="text"
          className="chat-input"
          placeholder="Напишете съобщение..."
          value={messageInput}
          onChange={(e) => setMessageInput(e.target.value)}
          onKeyDown={handleKeyDown}
        />
        <button 
          type="submit" 
          className="chat-send-button"
          disabled={!messageInput.trim()}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </form>
    </div>
  );
};
