import React, { useMemo, useState, useEffect, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { ExerciseDto, DayOfWeek, DAY_ORDER } from '../../../types/fitnessProgram';
import { ExerciseCard } from '../ExerciseCard';
import { getCompletedDaysForProgramWeek, completeWorkout } from '../../../services/fitnessProgramService';
import './ExercisesGrid.css';

interface ExercisesGridProps {
  exercises: ExerciseDto[];
  search: string;
  programId?: number;
  onWorkoutCompleted?: () => void;
}

export const ExercisesGrid: React.FC<ExercisesGridProps> = ({
  exercises,
  search,
  programId,
  onWorkoutCompleted
}) => {
  const { t } = useTranslation();
  const [completedDays, setCompletedDays] = useState<Set<DayOfWeek>>(new Set());
  const [submittingDay, setSubmittingDay] = useState<DayOfWeek | null>(null);

  const fetchCompletedDays = useCallback(async () => {
    if (programId == null) return;
    try {
      const days = await getCompletedDaysForProgramWeek(programId);
      setCompletedDays(days);
    } catch {
      setCompletedDays(new Set());
    }
  }, [programId]);

  useEffect(() => {
    fetchCompletedDays();
  }, [fetchCompletedDays]);

  const handleComplete = useCallback(
    async (dayOfWeek: DayOfWeek) => {
      if (programId == null) return;
      await completeWorkout(programId, dayOfWeek);
      setCompletedDays((prev) => new Set(prev).add(dayOfWeek));
      onWorkoutCompleted?.();
    },
    [programId, onWorkoutCompleted]
  );

  const filteredAndGroupedExercises = useMemo(() => {
    const filtered = exercises.filter(exercise =>
      exercise.name.toLowerCase().includes(search.toLowerCase()) ||
      (exercise.description && exercise.description.toLowerCase().includes(search.toLowerCase())) ||
      (exercise.muscleGroup && exercise.muscleGroup.toLowerCase().includes(search.toLowerCase()))
    );

    // Group by day
    const grouped: { [key: string]: ExerciseDto[] } = {};
    filtered.forEach(exercise => {
      const day = exercise.dayOfWeek;
      const dayKey = day.toString();
      if (!grouped[dayKey]) {
        grouped[dayKey] = [];
      }
      grouped[dayKey].push(exercise);
    });

    // Sort days
    const sortedDays = Object.keys(grouped).sort((a, b) => {
      const dayA = a as DayOfWeek;
      const dayB = b as DayOfWeek;
      return (DAY_ORDER[dayA] || 99) - (DAY_ORDER[dayB] || 99);
    });

    return sortedDays.map(day => ({
      day: day as DayOfWeek,
      dayLabel: t(`fitness.days.${(day as string).toLowerCase()}`),
      exercises: grouped[day]
    }));
  }, [exercises, search, t]);

  if (filteredAndGroupedExercises.length === 0 && search) {
    return (
      <div className="no-results">
        <p>{t('fitness.noExercisesFor')} "{search}"</p>
      </div>
    );
  }

  return (
    <div className="exercises-container">
      {filteredAndGroupedExercises.map(({ day, dayLabel, exercises: dayExercises }) => {
        const isDayCompleted = completedDays.has(day);
        const isSubmitting = submittingDay === day;
        const showDayButton = programId != null;

        return (
          <div key={day} className="day-exercises-section">
            <h3 className="day-title">{dayLabel}</h3>
            <div className="exercises-grid">
              {dayExercises.map((exercise) => (
                <ExerciseCard key={exercise.id} exercise={exercise} />
              ))}
            </div>
            {showDayButton && (
              <div className="day-complete-wrap">
                <button
                  type="button"
                  className={`exercise-complete-btn ${isDayCompleted ? 'exercise-complete-btn--completed' : ''}`}
                  disabled={isDayCompleted || isSubmitting}
                  onClick={async () => {
                    setSubmittingDay(day);
                    try {
                      await handleComplete(day);
                    } finally {
                      setSubmittingDay(null);
                    }
                  }}
                >
                  {isDayCompleted
                    ? t('fitness.workoutCompleted')
                    : t('fitness.completeWorkout')}
                </button>
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
};
