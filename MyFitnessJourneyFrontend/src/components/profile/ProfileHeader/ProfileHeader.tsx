import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import './ProfileHeader.css';

export const ProfileHeader: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <div className="profile-header">
      <button className="back-button" onClick={() => navigate('/dashboard')}>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 className="profile-title">{t('profile.title')}</h1>
      <p className="profile-subtitle">{t('profile.subtitle')}</p>
    </div>
  );
};
