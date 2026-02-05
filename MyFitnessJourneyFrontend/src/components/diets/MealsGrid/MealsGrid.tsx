import React from 'react';
import { useTranslation } from 'react-i18next';
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
  const { t } = useTranslation();
  const filteredMeals = meals.filter(meal =>
    meal.name.toLowerCase().includes(search.toLowerCase()) ||
    (meal.description && meal.description.toLowerCase().includes(search.toLowerCase()))
  );

  if (filteredMeals.length === 0 && search) {
    return (
      <div className="no-results">
        <p>{t('diets.noMealsFor')} "{search}"</p>
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

