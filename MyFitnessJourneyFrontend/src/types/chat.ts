export interface ChatMessageDto {
  id: number;
  senderId: number;
  senderUsername: string;
  recipientId: number;
  recipientUsername: string;
  content: string;
  sentAt: string;
  isRead: boolean;
}

export interface ChatUserDto {
  id: number;
  username: string;
  name: string;
  pictureUrl: string | null;
  lastMessageAt: string | null;
  unreadCount: number;
}

export interface SendMessageRequest {
  recipientId: number;
  content: string;
}
