import React from 'react';
import { MealDto } from '../../../services/dietService';
import { MealCard } from '../MealCard';
import './MealsGrid.css';

interface MealsGridProps {
  meals: MealDto[];
  search: string;
  onLogMeal: (mealId: number) => void;
  loggingMealId?: number | null;
}

export const MealsGrid: React.FC<MealsGridProps> = ({ meals, search, onLogMeal, loggingMealId }) => {
  const filteredMeals = meals.filter(meal =>
    meal.name.toLowerCase().includes(search.toLowerCase()) ||
    (meal.description && meal.description.toLowerCase().includes(search.toLowerCase()))
  );

  if (filteredMeals.length === 0 && search) {
    return (
      <div className="no-results">
        <p>Няма намерени ястия за "{search}"</p>
      </div>
    );
  }

  return (
    <div className="meals-grid">
      {filteredMeals.map((meal) => (
        <MealCard
          key={meal.id}
          meal={meal}
          onLogMeal={onLogMeal}
          isLogging={loggingMealId === meal.id}
        />
      ))}
    </div>
  );
};

