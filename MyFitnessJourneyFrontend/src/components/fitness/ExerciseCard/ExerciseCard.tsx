import React from 'react';
import { useTranslation } from 'react-i18next';
import { ExerciseDto } from '../../../types/fitnessProgram';
import './ExerciseCard.css';

interface ExerciseCardProps {
  exercise: ExerciseDto;
}

export const ExerciseCard: React.FC<ExerciseCardProps> = ({ exercise }) => {
  const { t } = useTranslation();
  const dayName = t(`fitness.days.${exercise.dayOfWeek.toLowerCase()}`);

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
              <span className="exercise-detail-label">{t('fitness.muscleGroup')}</span>
              <span className="exercise-detail-value">{exercise.muscleGroup}</span>
            </div>
          )}
          {exercise.sets !== undefined && exercise.sets !== null && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">{t('fitness.sets')}</span>
              <span className="exercise-detail-value">{exercise.sets}</span>
            </div>
          )}
          {exercise.reps !== undefined && exercise.reps !== null && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">{t('fitness.reps')}</span>
              <span className="exercise-detail-value">{exercise.reps}</span>
            </div>
          )}
          {exercise.weight !== undefined && exercise.weight !== null && (
            <div className="exercise-detail-item">
              <span className="exercise-detail-label">{t('fitness.weight')}</span>
              <span className="exercise-detail-value">
                {exercise.weight} {exercise.weightUnit || ''}
              </span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
