import React from 'react';
import './NutritionCircle.css';

interface NutritionCircleProps {
  value: number;
  target: number;
  label: string;
}

export const NutritionCircle: React.FC<NutritionCircleProps> = ({ value, target, label }) => {
  const circumference = 2 * Math.PI * 45;
  const percentage = Math.min(value / target, 1);
  const strokeDasharray = `${circumference * percentage} ${circumference}`;

  return (
    <div className="nutrition-circle">
      <div className="circle-wrapper">
        <svg className="circle-progress" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="45" stroke="#999" strokeWidth="8" fill="none"/>
          <circle 
            cx="50" 
            cy="50" 
            r="45" 
            stroke="#FF5722" 
            strokeWidth="8" 
            fill="none" 
            strokeDasharray={strokeDasharray} 
            strokeLinecap="round" 
            transform="rotate(-90 50 50)"
          />
        </svg>
        <div className="circle-value">{value}</div>
      </div>
      <div className="circle-label">{label}</div>
    </div>
  );
};

