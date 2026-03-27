import React from 'react';
import { DietDto } from '../../../services/dietService';
import { DietCard } from '../DietCard';
import './DietGrid.css';

interface DietGridProps {
  diets: DietDto[];
  search: string;
  onSelect: (diet: DietDto) => void;
}

export const DietGrid: React.FC<DietGridProps> = ({ diets, search, onSelect }) => {
  const filteredDiets = diets.filter(diet => 
    diet.name.toLowerCase().includes(search.toLowerCase()) ||
    diet.description.toLowerCase().includes(search.toLowerCase())
  );

  if (filteredDiets.length === 0 && search) {
    return (
      <div className="no-results">
        <p>Няма намерени диети за "{search}"</p>
      </div>
    );
  }

  return (
    <div className="diets-grid">
      {filteredDiets.map((diet) => (
        <DietCard key={diet.id} diet={diet} onClick={() => onSelect(diet)} />
      ))}
    </div>
  );
};

