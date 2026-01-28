import React, { useState, useRef, useEffect } from 'react';
import { ChatUserDto, ChatMessageDto } from '../../../types/chat';
import { MessageList } from '../MessageList';
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

  const getDisplayName = (user: ChatUserDto): string => {
    return user.username || user.name || 'Потребител';
  };

  const getInitial = (user: ChatUserDto): string => {
    const name = getDisplayName(user);
    return name.charAt(0).toUpperCase();
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
          <MessageList messages={messages} currentUserId={currentUserId} />
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
