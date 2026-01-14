import React from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchBar } from '../../common/SearchBar';
import './FitnessProgramsHeader.css';

interface FitnessProgramsHeaderProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  hasSelectedProgram: boolean;
}

export const FitnessProgramsHeader: React.FC<FitnessProgramsHeaderProps> = ({
  searchQuery,
  onSearchChange,
  hasSelectedProgram
}) => {
  const navigate = useNavigate();

  return (
    <div className="fitness-programs-header">
      <button className="back-button" onClick={() => navigate('/dashboard')}>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 className="fitness-programs-title">Фитнес Програми</h1>
      <p className="fitness-programs-subtitle">Изберете програма, която отговаря на вашите цели</p>
      
      <SearchBar
        value={searchQuery}
        onChange={onSearchChange}
        placeholder={hasSelectedProgram ? 'Търси упражнение...' : 'Търси програма...'}
      />
    </div>
  );
};
