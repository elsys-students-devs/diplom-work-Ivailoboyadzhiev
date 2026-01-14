import api from './authService';
import { ExerciseDto, FitnessProgramDto } from '../types/fitnessProgram';

export { ExerciseDto, FitnessProgramDto } from '../types/fitnessProgram';

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
