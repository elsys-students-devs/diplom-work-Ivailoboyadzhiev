import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './DropdownMenu.css';

interface DropdownMenuProps {
  open: boolean;
  onClose: () => void;
}

export const DropdownMenu: React.FC<DropdownMenuProps> = ({ open, onClose }) => {
  const navigate = useNavigate();

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
        Dashboard
      </div>
      <div className="dropdown-menu-item">Profile</div>
      <div className="dropdown-menu-item">Settings</div>
      <div className="dropdown-menu-item">Help</div>
      <div className="dropdown-menu-item">Logout</div>
    </div>
  );
};

