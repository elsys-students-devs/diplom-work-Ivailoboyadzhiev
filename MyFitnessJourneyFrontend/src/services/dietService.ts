import api from './authService';

export interface MealDto {
  id: number;
  name: string;
  description?: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber?: number;
  sugar?: number;
}

export interface DietDto {
  id: number;
  name: string;
  description: string;
  benefits?: string;
  meals: MealDto[];
}

export const getAllDiets = async (): Promise<DietDto[]> => {
  const response = await api.get<DietDto[]>('/diets');
  return response.data;
};

export const getDietById = async (id: number): Promise<DietDto> => {
  const response = await api.get<DietDto>(`/diets/${id}`);
  return response.data;
};

export const getMealsByDietId = async (id: number): Promise<MealDto[]> => {
  const response = await api.get<MealDto[]>(`/diets/${id}/meals`);
  return response.data;
};

export interface CreateMealRequest {
  name: string;
  description?: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber?: number;
  sugar?: number;
}

export const createMeal = async (request: CreateMealRequest): Promise<MealDto> => {
  const response = await api.post<MealDto>('/diets/meals/create', request);
  return response.data;
};

