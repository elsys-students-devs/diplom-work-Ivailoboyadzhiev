import React from 'react';
import { NutritionCircle } from '../NutritionCircle';
import './NutritionSummary.css';

interface NutritionSummaryProps {
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  targetCalories?: number;
  targetProtein?: number;
  targetCarbs?: number;
  targetFat?: number;
  onLogMeal: () => void;
}

export const NutritionSummary: React.FC<NutritionSummaryProps> = ({
  calories,
  protein,
  carbs,
  fat,
  targetCalories = 2000,
  targetProtein = 150,
  targetCarbs = 250,
  targetFat = 65,
  onLogMeal,
}) => {
  return (
    <div className="nutrition-section">
      <div className="nutrition-header">
        <h2 className="nutrition-title">Today's Nutrition</h2>
        <button className="log-meal-btn" onClick={onLogMeal}>Log Meal</button>
      </div>
      <div className="nutrition-circles">
        <NutritionCircle value={calories} target={targetCalories} label="CALORIES" />
        <NutritionCircle value={protein} target={targetProtein} label="PROTEIN" />
        <NutritionCircle value={carbs} target={targetCarbs} label="CARBS" />
        <NutritionCircle value={fat} target={targetFat} label="FAT" />
      </div>
    </div>
  );
};

