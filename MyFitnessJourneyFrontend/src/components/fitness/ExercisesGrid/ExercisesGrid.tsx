import React, { useMemo } from 'react';
import { ExerciseDto } from '../../../services/fitnessProgramService';
import { ExerciseCard } from '../ExerciseCard';
import './ExercisesGrid.css';

interface ExercisesGridProps {
  exercises: ExerciseDto[];
  search: string;
}

const dayOrder: { [key: string]: number } = {
  'Понеделник': 1,
  'Вторник': 2,
  'Сряда': 3,
  'Четвъртък': 4,
  'Петък': 5,
  'Събота': 6,
  'Неделя': 7
};

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
      if (!grouped[day]) {
        grouped[day] = [];
      }
      grouped[day].push(exercise);
    });

    // Sort days
    const sortedDays = Object.keys(grouped).sort((a, b) => 
      (dayOrder[a] || 99) - (dayOrder[b] || 99)
    );

    return sortedDays.map(day => ({
      day,
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
      {filteredAndGroupedExercises.map(({ day, exercises: dayExercises }) => (
        <div key={day} className="day-exercises-section">
          <h3 className="day-title">{day}</h3>
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
