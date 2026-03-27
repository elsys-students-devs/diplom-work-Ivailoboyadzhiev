import React, { useMemo } from 'react';
import { FitnessProgramDto } from '../../../types/fitnessProgram';
import { FitnessProgramCard } from '../FitnessProgramCard';
import './FitnessProgramGrid.css';

interface FitnessProgramGridProps {
  programs: FitnessProgramDto[];
  search: string;
  onSelect: (program: FitnessProgramDto) => void;
}

export const FitnessProgramGrid: React.FC<FitnessProgramGridProps> = ({ programs, search, onSelect }) => {
  const filteredPrograms = useMemo(() => {
    return programs.filter(program => 
      program.name.toLowerCase().includes(search.toLowerCase()) ||
      program.description.toLowerCase().includes(search.toLowerCase())
    );
  }, [programs, search]);

  if (filteredPrograms.length === 0 && search) {
    return (
      <div className="no-results">
        <p>Няма намерени програми за "{search}"</p>
      </div>
    );
  }

  return (
    <div className="fitness-programs-grid">
      {filteredPrograms.map((program) => (
        <FitnessProgramCard key={program.id} program={program} onClick={() => onSelect(program)} />
      ))}
    </div>
  );
};
