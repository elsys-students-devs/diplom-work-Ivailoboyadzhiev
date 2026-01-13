import api from './authService';

export interface ExerciseDto {
  id: number;
  name: string;
  description?: string;
  dayOfWeek: string;
  sets?: number;
  reps?: number;
  weight?: string;
  muscleGroup?: string;
}

export interface FitnessProgramDto {
  id: number;
  name: string;
  description: string;
  benefits?: string;
  exercises: ExerciseDto[];
}

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
