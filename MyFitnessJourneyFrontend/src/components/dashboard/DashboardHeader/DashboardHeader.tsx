import React from 'react';
import './DashboardHeader.css';

interface DashboardHeaderProps {
  username: string;
}

export const DashboardHeader: React.FC<DashboardHeaderProps> = ({ username }) => {
  return (
    <div className="dashboard-header">
      <div className="header-content">
        <p className="welcome-text">Welcome back</p>
        <h1 className="user-name">{username}</h1>
        <p className="motivational-text">Let's crush your fitness goals today!</p>
      </div>
    </div>
  );
};
