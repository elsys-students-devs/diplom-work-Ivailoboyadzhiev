import React from 'react';
import { useTranslation } from 'react-i18next';
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
  const { t } = useTranslation();
  return (
    <div className="nutrition-section">
      <div className="nutrition-header">
        <h2 className="nutrition-title">{t('dashboard.todayNutrition')}</h2>
        <button className="log-meal-btn" onClick={onLogMeal}>{t('dashboard.logMeal')}</button>
      </div>
      <div className="nutrition-circles">
        <NutritionCircle value={calories} target={targetCalories} label={t('dashboard.calories')} />
        <NutritionCircle value={protein} target={targetProtein} label={t('dashboard.protein')} />
        <NutritionCircle value={carbs} target={targetCarbs} label={t('dashboard.carbs')} />
        <NutritionCircle value={fat} target={targetFat} label={t('dashboard.fat')} />
      </div>
    </div>
  );
};

