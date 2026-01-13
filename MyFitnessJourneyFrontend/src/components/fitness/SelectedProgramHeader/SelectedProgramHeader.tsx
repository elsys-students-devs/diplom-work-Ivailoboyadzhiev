import React from 'react';
import { FitnessProgramDto } from '../../../services/fitnessProgramService';
import './SelectedProgramHeader.css';

interface SelectedProgramHeaderProps {
  program: FitnessProgramDto;
  onBack: () => void;
}

export const SelectedProgramHeader: React.FC<SelectedProgramHeaderProps> = ({ program, onBack }) => {
  return (
    <>
      <button className="back-to-programs" onClick={onBack}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        Назад към програми
      </button>
      <div className="selected-program-info">
        <h2 className="selected-program-title">{program.name}</h2>
        <p className="selected-program-description">{program.description}</p>
        {program.benefits && (
          <div className="selected-program-benefits">
            <strong>Предимства:</strong> {program.benefits}
          </div>
        )}
      </div>
    </>
  );
};
