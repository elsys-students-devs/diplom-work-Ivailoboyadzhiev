import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
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
  const { t, i18n } = useTranslation();
  const currentLanguage = i18n.language;
  const [programs, setPrograms] = useState<FitnessProgramDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedProgram, setSelectedProgram] = useState<FitnessProgramDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [searchParams, setSearchParams] = useSearchParams();
  const { toasts, showError, removeToast } = useToast();

  useEffect(() => {
    let cancelled = false;
    const fetchPrograms = async () => {
      try {
        setLoading(true);
        const data = await getAllFitnessPrograms();
        if (cancelled) return;
        setPrograms(data);
      } catch (error) {
        if (!cancelled) {
          console.error('Failed to fetch fitness programs:', error);
          showError(t('fitness.loadError'));
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchPrograms();
    return () => { cancelled = true; };
  }, [currentLanguage, showError, t]);

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
            .catch(() => showError(t('fitness.programNotFound')));
        }
      }
    }
  }, [searchParams, programs, selectedProgram, showError]);

  useEffect(() => {
    if (!selectedProgram) return;
    let cancelled = false;
    getFitnessProgramById(selectedProgram.id)
      .then((program) => {
        if (!cancelled) setSelectedProgram(program);
      })
      .catch(() => {
        if (!cancelled) showError(t('fitness.loadError'));
      });
    return () => { cancelled = true; };
  }, [currentLanguage, selectedProgram?.id]);

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
        <Loading message={t('fitness.loading')} />
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
          key={currentLanguage}
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
