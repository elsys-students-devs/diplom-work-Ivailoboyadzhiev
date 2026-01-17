import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getAllFitnessPrograms, getFitnessProgramById } from '../services/fitnessProgramService';
import { FitnessProgramDto } from '../types/fitnessProgram';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { Loading } from '../components/common/Loading';
import { FitnessProgramsHeader } from '../components/fitness/FitnessProgramsHeader';
import { FitnessProgramsMain } from '../components/fitness/FitnessProgramsMain';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './FitnessPrograms.css';

const FitnessPrograms: React.FC = () => {
  const [programs, setPrograms] = useState<FitnessProgramDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedProgram, setSelectedProgram] = useState<FitnessProgramDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [searchParams, setSearchParams] = useSearchParams();
  const { toasts, showError, removeToast } = useToast();

  useEffect(() => {
    const fetchPrograms = async () => {
      try {
        const data = await getAllFitnessPrograms();
        setPrograms(data);
      } catch (error) {
        console.error('Failed to fetch fitness programs:', error);
        showError('Грешка при зареждане на програмите');
      } finally {
        setLoading(false);
      }
    };

    fetchPrograms();
  }, [showError]);

  // Check if we should show specific program by ID
  useEffect(() => {
    const programIdParam = searchParams.get('programId');
    
    if (programIdParam && programs.length > 0 && !selectedProgram) {
      const programId = parseInt(programIdParam, 10);
      if (!isNaN(programId)) {
        const program = programs.find(p => p.id === programId);
        if (program) {
          setSelectedProgram(program);
        } else {
          // Try to fetch by ID if not in the list
          getFitnessProgramById(programId)
            .then(program => setSelectedProgram(program))
            .catch(() => showError('Програмата не беше намерена'));
        }
      }
    }
  }, [searchParams, programs, selectedProgram, showError]);

  const handleBackToPrograms = () => {
    setSelectedProgram(null);
    setSearchQuery('');
    const newSearchParams = new URLSearchParams(searchParams);
    newSearchParams.delete('programId');
    setSearchParams(newSearchParams);
  };

  if (loading) {
    return (
      <div className="fitness-programs-container">
        <Loading message="Зареждане..." />
      </div>
    );
  }

  return (
    <div className="fitness-programs-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      <FitnessProgramsHeader
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        hasSelectedProgram={!!selectedProgram}
      />

      <div className="fitness-programs-content">
        <FitnessProgramsMain
          programs={programs}
          selectedProgram={selectedProgram}
          searchQuery={searchQuery}
          onSelectProgram={setSelectedProgram}
          onBackToPrograms={handleBackToPrograms}
        />
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default FitnessPrograms;
