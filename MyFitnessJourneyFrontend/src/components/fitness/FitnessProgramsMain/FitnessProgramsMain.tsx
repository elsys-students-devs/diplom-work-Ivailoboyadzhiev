import React from 'react';
import { FitnessProgramDto } from '../../../types/fitnessProgram';
import { FitnessProgramGrid } from '../FitnessProgramGrid';
import { SelectedProgramHeader } from '../SelectedProgramHeader';
import { ExercisesGrid } from '../ExercisesGrid';

interface FitnessProgramsMainProps {
  programs: FitnessProgramDto[];
  selectedProgram: FitnessProgramDto | null;
  searchQuery: string;
  onSelectProgram: (program: FitnessProgramDto) => void;
  onBackToPrograms: () => void;
}

export const FitnessProgramsMain: React.FC<FitnessProgramsMainProps> = ({
  programs,
  selectedProgram,
  searchQuery,
  onSelectProgram,
  onBackToPrograms
}) => {
  if (!selectedProgram) {
    return (
      <FitnessProgramGrid
        programs={programs}
        search={searchQuery}
        onSelect={onSelectProgram}
      />
    );
  }

  return (
    <div className="exercises-view">
      <SelectedProgramHeader
        program={selectedProgram}
        onBack={onBackToPrograms}
      />
      <ExercisesGrid
        exercises={selectedProgram.exercises ?? []}
        search={searchQuery}
      />
    </div>
  );
};
