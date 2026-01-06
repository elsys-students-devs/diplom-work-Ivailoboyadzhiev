import React from 'react';
import { DietDto } from '../../services/dietService';
import './DietCard.css';

interface DietCardProps {
  diet: DietDto;
  onClick: () => void;
}

export const DietCard: React.FC<DietCardProps> = ({ diet, onClick }) => {
  return (
    <div className="diet-card" onClick={onClick}>
      <div className="diet-card-header">
        <h2 className="diet-card-title">{diet.name}</h2>
      </div>
      <div className="diet-card-body">
        <p className="diet-card-description">{diet.description}</p>
        {diet.benefits && (
          <div className="diet-card-benefits">
            <strong>Предимства:</strong>
            <p>{diet.benefits}</p>
          </div>
        )}
        <div className="diet-card-meals-count">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M6 2L3 6v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6l-3-4H6zM3 6h18M8 10v4M12 10v4M16 10v4"/>
          </svg>
          <span>{diet.meals?.length || 0} ястия</span>
        </div>
      </div>
      <div className="diet-card-footer">
        <span className="view-details">Виж детайли →</span>
      </div>
    </div>
  );
};

