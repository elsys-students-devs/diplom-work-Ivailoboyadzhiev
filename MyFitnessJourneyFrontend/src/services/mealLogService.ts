import api from './authService';

export interface NutritionSummary {
  totalCalories: number;
  totalProtein: number;
  totalCarbs: number;
  totalFat: number;
}

export interface LogMealRequest {
  mealId: number;
  date?: string; // ISO date string, optional
}

export const logMeal = async (mealId: number, date?: string): Promise<void> => {
  const request: LogMealRequest = { mealId };
  if (date) {
    request.date = date;
  }
  await api.post('/meals/log', request);
};

export const getTodayNutritionSummary = async (): Promise<NutritionSummary> => {
  const response = await api.get<NutritionSummary>('/meals/today/summary');
  return response.data;
};

