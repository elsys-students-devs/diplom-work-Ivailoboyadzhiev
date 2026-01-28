import React, { useMemo } from 'react';
import { ChatMessageDto } from '../../../types/chat';
import { MessageItem } from '../MessageItem';
import './MessageList.css';

interface MessageListProps {
  messages: ChatMessageDto[];
  currentUserId: number;
}

export const MessageList: React.FC<MessageListProps> = ({ messages, currentUserId }) => {
  const parseDateTime = (dateString: string): Date => {
    if (dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString)) {
      return new Date(dateString);
    }
    const cleanedDateString = dateString.split('.')[0] + 'Z';
    return new Date(cleanedDateString);
  };

  const formatDate = (dateString: string): string => {
    const date = parseDateTime(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Днес';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Вчера';
    } else {
      return date.toLocaleDateString('bg-BG', { day: 'numeric', month: 'long', year: 'numeric' });
    }
  };


  const messageGroups = useMemo(() => {
    const groups = new Map<string, ChatMessageDto[]>();
    messages.forEach((message) => {
      const dateKey = parseDateTime(message.sentAt).toDateString();
      const existing = groups.get(dateKey);
      if (existing) {
        existing.push(message);
      } else {
        groups.set(dateKey, [message]);
      }
    });
    return groups;
  }, [messages]);

  return (
    <>
      {Array.from<[string, ChatMessageDto[]]>(messageGroups.entries()).map(([dateKey, dateMessages]) => (
        <div key={dateKey} className="message-date-group">
          <div className="message-date-divider">
            <span>{formatDate(dateMessages[0].sentAt)}</span>
          </div>
          {dateMessages.map((message) => (
            <MessageItem
              key={message.id}
              message={message}
              isOwnMessage={message.senderId === currentUserId}
            />
          ))}
        </div>
      ))}
    </>
  );
};
