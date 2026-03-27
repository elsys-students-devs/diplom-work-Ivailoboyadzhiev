import React from 'react';
import { DietDto } from '../../../services/dietService';
import { DietGrid } from '../DietGrid';
import { SelectedDietHeader } from '../SelectedDietHeader';
import { MealsGrid } from '../MealsGrid';
import './DietsMain.css';

interface DietsMainProps {
  diets: DietDto[];
  selectedDiet: DietDto | null;
  searchQuery: string;
  onSelectDiet: (diet: DietDto) => void;
  onBackToDiets: () => void;
  onLogMeal: (mealId: number) => void;
  loggingMealId: number | null;
}

export const DietsMain: React.FC<DietsMainProps> = ({
  diets,
  selectedDiet,
  searchQuery,
  onSelectDiet,
  onBackToDiets,
  onLogMeal,
  loggingMealId
}) => {
  if (!selectedDiet) {
    return (
      <div className="diets-content">
        <DietGrid
          diets={diets}
          search={searchQuery}
          onSelect={onSelectDiet}
        />
      </div>
    );
  }

  return (
    <div className="diets-content">
      <div className="meals-view">
        <SelectedDietHeader
          diet={selectedDiet}
          onBack={onBackToDiets}
        />
        <MealsGrid
          meals={selectedDiet.meals ?? []}
          search={searchQuery}
          onLogMeal={onLogMeal}
          loggingMealId={loggingMealId}
        />
      </div>
    </div>
  );
};
