import React from 'react';
import { FitnessProgramDto } from '../../../services/fitnessProgramService';
import './FitnessProgramCard.css';

interface FitnessProgramCardProps {
  program: FitnessProgramDto;
  onClick: () => void;
}

export const FitnessProgramCard: React.FC<FitnessProgramCardProps> = ({ program, onClick }) => {
  return (
    <div className="fitness-program-card" onClick={onClick}>
      <div className="fitness-program-card-header">
        <h2 className="fitness-program-card-title">{program.name}</h2>
      </div>
      <div className="fitness-program-card-body">
        <p className="fitness-program-card-description">{program.description}</p>
        {program.benefits && (
          <div className="fitness-program-card-benefits">
            <strong>Предимства:</strong>
            <p>{program.benefits}</p>
          </div>
        )}
        <div className="fitness-program-card-exercises-count">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
          <span>{program.exercises?.length || 0} упражнения</span>
        </div>
      </div>
      <div className="fitness-program-card-footer">
        <span className="view-details">Виж детайли →</span>
      </div>
    </div>
  );
};
