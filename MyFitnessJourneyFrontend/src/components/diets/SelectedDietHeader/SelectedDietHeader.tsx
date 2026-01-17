import React from 'react';
import { DietDto } from '../../../services/dietService';
import './SelectedDietHeader.css';

interface SelectedDietHeaderProps {
  diet: DietDto;
  onBack: () => void;
}

export const SelectedDietHeader: React.FC<SelectedDietHeaderProps> = ({ diet, onBack }) => {
  return (
    <>
      <button className="back-to-diets" onClick={onBack}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        Назад към диети
      </button>
      <div className="selected-diet-info">
        <h2 className="selected-diet-title">{diet.name}</h2>
        <p className="selected-diet-description">{diet.description}</p>
        {diet.benefits && (
          <div className="selected-diet-benefits">
            <strong>Предимства:</strong> {diet.benefits}
          </div>
        )}
      </div>
    </>
  );
};
