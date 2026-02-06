import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import './DropdownMenu.css';

interface DropdownMenuProps {
  open: boolean;
  onClose: () => void;
}

export const DropdownMenu: React.FC<DropdownMenuProps> = ({ open, onClose }) => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (open && !target.closest('.hamburger-menu-btn') && !target.closest('.dropdown-menu')) {
        onClose();
      }
    };

    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="dropdown-menu">
      <div className="dropdown-menu-item" onClick={() => { navigate('/dashboard'); onClose(); }}>
        {t('menu.dashboard')}
      </div>
      <div className="dropdown-menu-item" onClick={() => { navigate('/diets'); onClose(); }}>
        {t('menu.diets')}
      </div>
      <div className="dropdown-menu-item" onClick={() => { navigate('/fitness-programs'); onClose(); }}>
        {t('menu.fitnessPrograms')}
      </div>
      <div className="dropdown-menu-item" onClick={() => { navigate('/chat'); onClose(); }}>
        {t('menu.messages')}
      </div>
      <div className="dropdown-menu-item" onClick={() => { navigate('/profile'); onClose(); }}>
        {t('menu.profile')}
      </div>
      <div className="dropdown-menu-item">{t('menu.settings')}</div>
      <div className="dropdown-menu-item">{t('menu.help')}</div>
      <div className="dropdown-menu-item" onClick={async () => {
        try {
          const { logout } = await import('../../services/authService');
          await logout();
        } finally {
          navigate('/login');
          onClose();
        }
      }}>
        {t('menu.logout')}
      </div>
    </div>
  );
};

