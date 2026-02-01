import React from 'react';
import { useNavigate } from 'react-router-dom';
import { SummaryCards } from '../SummaryCards';
import { NutritionSummary } from '../NutritionSummary';
import './DashboardMain.css';

interface DashboardMainProps {
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  targetCalories: number;
  targetProtein: number;
  targetCarbs: number;
  targetFat: number;
  unreadMessages: number;
  streak: number;
  onLogMeal: () => void;
}

export const DashboardMain: React.FC<DashboardMainProps> = ({
  calories,
  protein,
  carbs,
  fat,
  targetCalories,
  targetProtein,
  targetCarbs,
  targetFat,
  unreadMessages,
  streak,
  onLogMeal
}) => {
  const navigate = useNavigate();

  return (
    <div className="dashboard-content">
      <SummaryCards messages={unreadMessages} streak={streak} />
      
      <NutritionSummary
        calories={calories}
        protein={protein}
        carbs={carbs}
        fat={fat}
        targetCalories={targetCalories}
        targetProtein={targetProtein}
        targetCarbs={targetCarbs}
        targetFat={targetFat}
        onLogMeal={onLogMeal}
      />

      {/* Action Buttons */}
      <div className="action-buttons">
        <button className="action-button diet-plans" onClick={() => navigate('/diets')}>
          <div className="button-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M6 2L3 6v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6l-3-4H6zM3 6h18M8 10v4M12 10v4M16 10v4" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div className="button-content">
            <div className="button-title">Diet Plans</div>
            <div className="button-subtitle">View meal plans & nutrition</div>
          </div>
          <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>

        <button className="action-button fitness-programs" onClick={() => navigate('/fitness-programs')}>
          <div className="button-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div className="button-content">
            <div className="button-title">Fitness Programs</div>
            <div className="button-subtitle">Workouts & training</div>
          </div>
          <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>

        <button className="action-button messages" onClick={() => navigate('/chat')}>
          <div className="button-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div className="button-content">
            <div className="button-title">Messages</div>
            <div className="button-subtitle">Chat with trainer</div>
          </div>
          <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
  );
};
