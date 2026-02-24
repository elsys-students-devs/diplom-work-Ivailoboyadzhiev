import api from './authService';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

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
}

export interface SendMessageRequest {
  recipientId: number;
  content: string;
}

let stompClient: Client | null = null;

export const connectWebSocket = (
  userId: number,
  onMessageReceived: (message: ChatMessageDto) => void
): Client => {
  const client = new Client({
    webSocketFactory: () => new SockJS(`${BACKEND_URL}/ws`),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = () => {
    // Subscribe to user-specific topic
    client?.subscribe(`/topic/chat/${userId}`, (message) => {
      const chatMessage: ChatMessageDto = JSON.parse(message.body);
      onMessageReceived(chatMessage);
    });
  };

  client.onStompError = (frame) => {
    console.error('STOMP error:', frame.headers['message']);
  };

  client.activate();
  stompClient = client;
  
  return client;
};

export const disconnectWebSocket = (): void => {
  if (stompClient && stompClient.connected) {
    stompClient.deactivate();
    stompClient = null;
  }
};

export const sendMessage = async (request: SendMessageRequest): Promise<ChatMessageDto> => {
  const response = await api.post<ChatMessageDto>('/chat/messages', request);
  return response.data;
};

export const getConversation = async (chatPartnerId: number): Promise<ChatMessageDto[]> => {
  const response = await api.get<ChatMessageDto[]>(`/chat/conversations/${chatPartnerId}`);
  return response.data;
};

export const markMessagesAsRead = async (chatPartnerId: number): Promise<void> => {
  await api.put(`/chat/messages/read/${chatPartnerId}`);
};

export const getChatPartners = async (): Promise<ChatUserDto[]> => {
  const response = await api.get<ChatUserDto[]>('/chat/partners');
  return response.data;
};

export const searchUsers = async (query: string): Promise<ChatUserDto[]> => {
  const response = await api.get<ChatUserDto[]>('/chat/users', {
    params: { query }
  });
  return response.data;
};

export const getUnreadCount = async (): Promise<number> => {
  const response = await api.get<{ count: number }>('/chat/messages/unread/count');
  return response.data.count;
};
