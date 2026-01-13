import React from 'react';
import { ExerciseDto } from '../../../services/fitnessProgramService';
import './ExerciseCard.css';

interface ExerciseCardProps {
  exercise: ExerciseDto;
}

export const ExerciseCard: React.FC<ExerciseCardProps> = ({ exercise }) => {
  const dayName = exercise.dayOfWeek;

  return (
    <div className="exercise-card">
      <div className="exercise-card-header">
        <h3 className="exercise-card-title">{exercise.name}</h3>
        <span className="exercise-day-badge">{dayName}</span>
      </div>
      <div className="exercise-card-body">
        {exercise.description && (
          <p className="exercise-card-description">{exercise.description}</p>
        )}
        <div className="exercise-details">
          {exercise.muscleGroup && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">Мускулна група:</span>
              <span className="exercise-detail-value">{exercise.muscleGroup}</span>
            </div>
          )}
          {exercise.sets && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">Серии:</span>
              <span className="exercise-detail-value">{exercise.sets}</span>
            </div>
          )}
          {exercise.reps && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">Повторения:</span>
              <span className="exercise-detail-value">{exercise.reps}</span>
            </div>
          )}
          {exercise.weight && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">Тежест:</span>
              <span className="exercise-detail-value">{exercise.weight}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
