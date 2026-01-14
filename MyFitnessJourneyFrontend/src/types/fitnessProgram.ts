export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY'
}

export const DAY_LABELS: Record<DayOfWeek, string> = {
  [DayOfWeek.MONDAY]: 'Понеделник',
  [DayOfWeek.TUESDAY]: 'Вторник',
  [DayOfWeek.WEDNESDAY]: 'Сряда',
  [DayOfWeek.THURSDAY]: 'Четвъртък',
  [DayOfWeek.FRIDAY]: 'Петък',
  [DayOfWeek.SATURDAY]: 'Събота',
  [DayOfWeek.SUNDAY]: 'Неделя'
};

export const DAY_ORDER: Record<DayOfWeek, number> = {
  [DayOfWeek.MONDAY]: 1,
  [DayOfWeek.TUESDAY]: 2,
  [DayOfWeek.WEDNESDAY]: 3,
  [DayOfWeek.THURSDAY]: 4,
  [DayOfWeek.FRIDAY]: 5,
  [DayOfWeek.SATURDAY]: 6,
  [DayOfWeek.SUNDAY]: 7
};

export interface ExerciseDto {
  id: number;
  name: string;
  description?: string;
  dayOfWeek: DayOfWeek;
  sets?: number | null;
  reps?: number | null;
  weight?: number | null;
  weightUnit?: string;
  muscleGroup?: string;
}

export interface FitnessProgramDto {
  id: number;
  name: string;
  description: string;
  benefits?: string;
  exercises: ExerciseDto[];
}
