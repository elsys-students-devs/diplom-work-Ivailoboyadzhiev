import React from 'react';
import { useTranslation } from 'react-i18next';
import './HamburgerMenu.css';

interface HamburgerMenuProps {
  open: boolean;
  onToggle: () => void;
}

export const HamburgerMenu: React.FC<HamburgerMenuProps> = ({ open, onToggle }) => {
  const { t } = useTranslation();
  return (
    <button 
      className="hamburger-menu-btn" 
      onClick={onToggle}
      aria-label={t('menu.toggleMenu')}
    >
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="3" y1="6" x2="21" y2="6"></line>
        <line x1="3" y1="12" x2="21" y2="12"></line>
        <line x1="3" y1="18" x2="21" y2="18"></line>
      </svg>
    </button>
  );
};

