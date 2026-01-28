import React from 'react';
import { ChatMessageDto } from '../../../types/chat';
import './MessageItem.css';

interface MessageItemProps {
  message: ChatMessageDto;
  isOwnMessage: boolean;
}

export const MessageItem: React.FC<MessageItemProps> = ({ message, isOwnMessage }) => {
  const formatTime = (dateString: string): string => {
    const parseDateTime = (dateStr: string): Date => {
      if (dateStr.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateStr)) {
        return new Date(dateStr);
      }
      const cleanedDateString = dateStr.split('.')[0] + 'Z';
      return new Date(cleanedDateString);
    };

    const date = parseDateTime(dateString);
    return date.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={`message ${isOwnMessage ? 'sent' : 'received'}`}>
      <div className="message-content">
        <p>{message.content}</p>
        <span className="message-time">{formatTime(message.sentAt)}</span>
      </div>
    </div>
  );
};
