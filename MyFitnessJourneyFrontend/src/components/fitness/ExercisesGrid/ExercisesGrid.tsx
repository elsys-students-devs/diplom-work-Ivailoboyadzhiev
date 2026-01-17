import React, { useMemo } from 'react';
import { ExerciseDto, DayOfWeek, DAY_LABELS, DAY_ORDER } from '../../../types/fitnessProgram';
import { ExerciseCard } from '../ExerciseCard';
import './ExercisesGrid.css';

interface ExercisesGridProps {
  exercises: ExerciseDto[];
  search: string;
}

export const ExercisesGrid: React.FC<ExercisesGridProps> = ({ exercises, search }) => {
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
      dayLabel: DAY_LABELS[day as DayOfWeek],
      exercises: grouped[day]
    }));
  }, [exercises, search]);

  if (filteredAndGroupedExercises.length === 0 && search) {
    return (
      <div className="no-results">
        <p>Няма намерени упражнения за "{search}"</p>
      </div>
    );
  }

  return (
    <div className="exercises-container">
      {filteredAndGroupedExercises.map(({ day, dayLabel, exercises: dayExercises }) => (
        <div key={day} className="day-exercises-section">
          <h3 className="day-title">{dayLabel}</h3>
          <div className="exercises-grid">
            {dayExercises.map((exercise) => (
              <ExerciseCard key={exercise.id} exercise={exercise} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};
