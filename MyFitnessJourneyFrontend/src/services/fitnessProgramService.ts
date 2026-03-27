import api from './authService';
import { DayOfWeek, ExerciseDto, FitnessProgramDto } from '../types/fitnessProgram';

export const getAllFitnessPrograms = async (): Promise<FitnessProgramDto[]> => {
  const response = await api.get<FitnessProgramDto[]>('/fitness-programs');
  return response.data;
};

export const getFitnessProgramById = async (id: number): Promise<FitnessProgramDto> => {
  const response = await api.get<FitnessProgramDto>(`/fitness-programs/${id}`);
  return response.data;
};

export const getExercisesByFitnessProgramId = async (id: number): Promise<ExerciseDto[]> => {
  const response = await api.get<ExerciseDto[]>(`/fitness-programs/${id}/exercises`);
  return response.data;
};

export const getCompletedWorkoutsCount = async (): Promise<number> => {
  const response = await api.get<{ count: number }>('/workouts/count');
  return response.data.count;
};

export const getCompletedDaysForProgramWeek = async (
  programId: number,
  weekStart?: string
): Promise<Set<DayOfWeek>> => {
  const params = weekStart ? { weekStart } : {};
  const response = await api.get<{ completedDays: DayOfWeek[] }>(
    `/workouts/programs/${programId}/completed-days`,
    { params }
  );
  return new Set(response.data.completedDays || []);
};

export const completeWorkout = async (
  programId: number,
  dayOfWeek: DayOfWeek
): Promise<void> => {
  await api.post(`/workouts/programs/${programId}/complete`, { dayOfWeek });
};
