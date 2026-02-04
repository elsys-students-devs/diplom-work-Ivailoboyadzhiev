import React, { useState } from 'react';
import './DashboardHeader.css';

interface DashboardHeaderProps {
  username: string;
  pictureUrl?: string | null;
}

export const DashboardHeader: React.FC<DashboardHeaderProps> = ({ username, pictureUrl }) => {
  const [imageError, setImageError] = useState(false);
  const initial = (username && username[0]) ? username[0].toUpperCase() : '?';
  const showImage = pictureUrl && !imageError;
  return (
    <div className="dashboard-header">
      <div className="header-content">
        {showImage ? (
          <img src={pictureUrl!} alt="" className="dashboard-header-avatar" onError={() => setImageError(true)} />
        ) : (
          <div className="dashboard-header-avatar dashboard-header-avatar-placeholder">{initial}</div>
        )}
        <div className="header-text">
          <p className="welcome-text">Welcome back</p>
          <h1 className="user-name">{username}</h1>
          <p className="motivational-text">Let's crush your fitness goals today!</p>
        </div>
      </div>
    </div>
  );
};
