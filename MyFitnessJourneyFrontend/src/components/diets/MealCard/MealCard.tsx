import React from 'react';
import { MealDto } from '../../../services/dietService';
import './MealCard.css';

interface MealCardProps {
  meal: MealDto;
  onLogMeal: (mealId: number) => void;
  isLogging?: boolean;
}

export const MealCard: React.FC<MealCardProps> = ({ meal, onLogMeal, isLogging = false }) => {
  return (
    <div className="meal-card">
      <div className="meal-card-header">
        <h3 className="meal-card-title">{meal.name}</h3>
      </div>
      <div className="meal-card-body">
        {meal.description && (
          <p className="meal-card-description">{meal.description}</p>
        )}
        <div className="meal-macros">
          <div className="macro-item">
            <span className="macro-label">Калории</span>
            <span className="macro-value">{meal.calories} kcal</span>
          </div>
          <div className="macro-item">
            <span className="macro-label">Протеин</span>
            <span className="macro-value">{meal.protein}g</span>
          </div>
          <div className="macro-item">
            <span className="macro-label">Въглехидрати</span>
            <span className="macro-value">{meal.carbs}g</span>
          </div>
          <div className="macro-item">
            <span className="macro-label">Мазнини</span>
            <span className="macro-value">{meal.fat}g</span>
          </div>
          {meal.fiber && (
            <div className="macro-item">
              <span className="macro-label">Фибри</span>
              <span className="macro-value">{meal.fiber}g</span>
            </div>
          )}
          {meal.sugar && (
            <div className="macro-item">
              <span className="macro-label">Захар</span>
              <span className="macro-value">{meal.sugar}g</span>
            </div>
          )}
        </div>
      </div>
      <div className="meal-card-footer">
        <button 
          className="log-meal-button"
          onClick={() => onLogMeal(meal.id)}
          disabled={isLogging}
        >
          {isLogging ? 'Логване...' : 'Логни ястие'}
        </button>
      </div>
    </div>
  );
};
