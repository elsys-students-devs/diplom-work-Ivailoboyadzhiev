import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { SearchBar } from '../../common/SearchBar';
import './DietsHeader.css';

interface DietsHeaderProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  hasSelectedDiet: boolean;
}

export const DietsHeader: React.FC<DietsHeaderProps> = ({
  searchQuery,
  onSearchChange,
  hasSelectedDiet
}) => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <div className="diets-header">
      <button className="back-button" onClick={() => navigate('/dashboard')}>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 className="diets-title">{t('diets.title')}</h1>
      <p className="diets-subtitle">{t('diets.subtitle')}</p>
      
      <SearchBar
        value={searchQuery}
        onChange={onSearchChange}
        placeholder={hasSelectedDiet ? t('diets.searchMeal') : t('diets.searchDiet')}
      />
    </div>
  );
};
