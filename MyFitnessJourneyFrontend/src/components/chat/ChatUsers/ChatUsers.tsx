import React, { useState, useEffect, useCallback, useRef } from 'react';
import { UserList } from '../UserList';
import { ChatUserDto } from '../../../types/chat';
import { getChatPartners, searchUsers } from '../../../services/chatService';
import './ChatUsers.css';

interface ChatUsersProps {
  selectedUserId: number | null;
  onSelectUser: (user: ChatUserDto) => void;
  onClose?: () => void;
  onRefreshRef?: React.MutableRefObject<(() => void) | null>;
}

export const ChatUsers: React.FC<ChatUsersProps> = ({ 
  selectedUserId, 
  onSelectUser,
  onClose,
  onRefreshRef
}) => {
  const [users, setUsers] = useState<ChatUserDto[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const isFirstLoadRef = useRef(true);
  const searchQueryRef = useRef('');

  useEffect(() => {
    searchQueryRef.current = searchQuery;
  }, [searchQuery]);

  const loadUsers = useCallback(async (showLoading = true, forceLoadPartners = false) => {
    if (showLoading && isFirstLoadRef.current) {
      setIsLoading(true);
    }
    try {
      const currentQuery = searchQueryRef.current;
      if (forceLoadPartners || !currentQuery.trim()) {
        const partners = await getChatPartners();
        setUsers(partners);
      } else {
        const results = await searchUsers(currentQuery);
        setUsers(results);
      }
      isFirstLoadRef.current = false;
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (onRefreshRef) {
      onRefreshRef.current = async () => {
        const isSearching = searchQueryRef.current.trim();
        if (!isSearching) {
          try {
            const partners = await getChatPartners();
            setUsers(partners);
          } catch (error) {
            console.error('Failed to refresh users:', error);
          }
        }
      };
    }
  }, [onRefreshRef]);

  useEffect(() => {
    const debounceTimer = setTimeout(() => loadUsers(true), 300);
    return () => clearTimeout(debounceTimer);
  }, [searchQuery, loadUsers]);

  return (
    <div className="chat-users-container">
      <UserList
        users={users}
        selectedUserId={selectedUserId}
        onSelectUser={onSelectUser}
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        isLoading={isLoading}
      />
    </div>
  );
};
