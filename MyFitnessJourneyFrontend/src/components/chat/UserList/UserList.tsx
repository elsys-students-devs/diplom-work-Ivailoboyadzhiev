import React, { useState, useCallback } from 'react';
import { ChatUserDto } from '../../../types/chat';
import { getProfilePictureUrl } from '../../../services/authService';
import './UserList.css';

interface UserListProps {
  users: ChatUserDto[];
  selectedUserId: number | null;
  onSelectUser: (user: ChatUserDto) => void;
  searchQuery: string;
  onSearchChange: (query: string) => void;
  isLoading: boolean;
}

export const UserList: React.FC<UserListProps> = ({
  users,
  selectedUserId,
  onSelectUser,
  searchQuery,
  onSearchChange,
  isLoading
}) => {
  const [failedImageIds, setFailedImageIds] = useState<Set<number>>(new Set());

  const getDisplayName = (user: ChatUserDto): string => {
    return user.username || user.name || 'Потребител';
  };

  const getInitial = (user: ChatUserDto): string => {
    const name = getDisplayName(user);
    return name.charAt(0).toUpperCase();
  };

  const handleImageError = useCallback((userId: number) => {
    setFailedImageIds((prev) => new Set(prev).add(userId));
  }, []);

  return (
    <div className="user-list-container">
      <div className="user-search-container">
        <svg className="user-search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="11" cy="11" r="8"></circle>
          <path d="m21 21-4.35-4.35"></path>
        </svg>
        <input
          type="text"
          className="user-search-input"
          placeholder="Търси потребители..."
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
        />
        {searchQuery && (
          <button className="user-search-clear" onClick={() => onSearchChange('')}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        )}
      </div>

      <div className="user-list">
        {isLoading ? (
          <div className="user-list-loading">
            <div className="loading-spinner"></div>
            <span>Зареждане...</span>
          </div>
        ) : users.length === 0 ? (
          <div className="user-list-empty">
            {searchQuery ? 'Няма намерени потребители' : 'Няма чатове'}
          </div>
        ) : (
          users.map((user) => {
            const avatarUrl = getProfilePictureUrl(user.pictureUrl);
            return (
            <div
              key={user.id}
              className={`user-item ${selectedUserId === user.id ? 'selected' : ''}`}
              onClick={() => onSelectUser(user)}
            >
              <div className="user-avatar">
                {avatarUrl && !failedImageIds.has(user.id) ? (
                  <img src={avatarUrl} alt={getDisplayName(user)} onError={() => handleImageError(user.id)} />
                ) : (
                  <span>{getInitial(user)}</span>
                )}
              </div>
              <div className="user-info">
                <span className="user-name">{getDisplayName(user)}</span>
              </div>
              {user.unreadCount > 0 && (
                <div className="user-unread-badge">
                  {user.unreadCount > 99 ? '99+' : user.unreadCount}
                </div>
              )}
            </div>
          );
          })
        )}
      </div>
    </div>
  );
};
