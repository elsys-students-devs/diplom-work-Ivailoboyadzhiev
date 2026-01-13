import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getAllFitnessPrograms, getFitnessProgramById, FitnessProgramDto } from '../services/fitnessProgramService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { SearchBar } from '../components/common/SearchBar';
import { Loading } from '../components/common/Loading';
import { FitnessProgramGrid } from '../components/fitness/FitnessProgramGrid';
import { SelectedProgramHeader } from '../components/fitness/SelectedProgramHeader';
import { ExercisesGrid } from '../components/fitness/ExercisesGrid';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './FitnessPrograms.css';

const FitnessPrograms: React.FC = () => {
  const [programs, setPrograms] = useState<FitnessProgramDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedProgram, setSelectedProgram] = useState<FitnessProgramDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const navigate = useNavigate();
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

      {/* Header Section */}
      <div className="fitness-programs-header">
        <button className="back-button" onClick={() => navigate('/dashboard')}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
        </button>
        <h1 className="fitness-programs-title">Фитнес Програми</h1>
        <p className="fitness-programs-subtitle">Изберете програма, която отговаря на вашите цели</p>
        
        <SearchBar
          value={searchQuery}
          onChange={setSearchQuery}
          placeholder={selectedProgram ? 'Търси упражнение...' : 'Търси програма...'}
        />
      </div>

      {/* Main Content */}
      <div className="fitness-programs-content">
        {!selectedProgram ? (
          <FitnessProgramGrid
            programs={programs}
            search={searchQuery}
            onSelect={setSelectedProgram}
          />
        ) : (
          <div className="exercises-view">
            <SelectedProgramHeader
              program={selectedProgram}
              onBack={handleBackToPrograms}
            />
            <ExercisesGrid
              exercises={selectedProgram.exercises ?? []}
              search={searchQuery}
            />
          </div>
        )}
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default FitnessPrograms;
