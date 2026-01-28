import React from 'react';
import { useNavigate } from 'react-router-dom';
import './ChatHeader.css';

interface ChatHeaderProps {
  title: string;
  subtitle?: string;
}

export const ChatHeader: React.FC<ChatHeaderProps> = ({ title, subtitle }) => {
  const navigate = useNavigate();

  return (
    <div className="chat-header">
      <button className="back-button" onClick={() => navigate('/dashboard')}>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 className="chat-title">{title}</h1>
      {subtitle && <p className="chat-subtitle">{subtitle}</p>}
    </div>
  );
};
