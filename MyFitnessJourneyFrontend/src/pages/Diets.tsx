import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { getAllDiets, getDietById, DietDto } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { Loading } from '../components/common/Loading';
import { DietsHeader } from '../components/diets/DietsHeader';
import { DietsMain } from '../components/diets/DietsMain';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './Diets.css';

const Diets: React.FC = () => {
  const { t, i18n } = useTranslation();
  const currentLanguage = i18n.language;
  const [diets, setDiets] = useState<DietDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDiet, setSelectedDiet] = useState<DietDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [loggingMeal, setLoggingMeal] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [searchParams, setSearchParams] = useSearchParams();
  const { toasts, showSuccess, showError, removeToast } = useToast();

  useEffect(() => {
    let cancelled = false;
    const fetchDiets = async () => {
      try {
        setLoading(true);
        const data = await getAllDiets();
        if (cancelled) return;
        setDiets(data);
      } catch (error) {
        if (!cancelled) {
          console.error('Failed to fetch diets:', error);
          showError(t('diets.loadError'));
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchDiets();
    return () => { cancelled = true; };
  }, [currentLanguage, showError, t]);


  useEffect(() => {
    if (!selectedDiet) return;
    let cancelled = false;
    getDietById(selectedDiet.id)
      .then((diet) => {
        if (!cancelled) setSelectedDiet(diet);
      })
      .catch(() => {
        if (!cancelled) showError(t('diets.loadError'));
      });
    return () => { cancelled = true; };
  }, [currentLanguage, selectedDiet?.id]);

  // Check if we should show specific diet by ID
  useEffect(() => {
    const dietIdParam = searchParams.get('dietId');
    const logMealParam = searchParams.get('logMeal');
    
    if (dietIdParam && diets.length > 0 && !selectedDiet) {
      const dietId = parseInt(dietIdParam, 10);
      if (!isNaN(dietId)) {
        const diet = diets.find(d => d.id === dietId);
        if (diet) {
          setSelectedDiet(diet);
        } else {
          // Try to fetch by ID if not in the list
          getDietById(dietId)
            .then(diet => setSelectedDiet(diet))
            .catch(() => showError(t('diets.dietNotFound')));
        }
      }
    } else if (logMealParam === 'true' && diets.length > 0 && !selectedDiet) {
      // Auto-select first diet if coming from dashboard to log meal
      setSelectedDiet(diets[0]);
    }
  }, [searchParams, diets, selectedDiet, showError]);

  const handleLogMeal = async (mealId: number) => {
    setLoggingMeal(mealId);
    try {
      await logMeal(mealId);
      showSuccess(t('diets.mealLogged'));
      setLoggingMeal(null);
    } catch (error) {
      console.error('Failed to log meal:', error);
      showError(t('diets.logError'));
      setLoggingMeal(null);
    }
  };

  const handleBackToDiets = () => {
    setSelectedDiet(null);
    setSearchQuery('');
    const newSearchParams = new URLSearchParams(searchParams);
    newSearchParams.delete('dietId');
    setSearchParams(newSearchParams);
  };

  if (loading) {
    return (
      <div className="diets-container">
        <Loading message={t('diets.loading')} />
      </div>
    );
  }

  return (
    <div className="diets-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      <DietsHeader
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        hasSelectedDiet={!!selectedDiet}
      />

      <DietsMain
        key={currentLanguage}
        diets={diets}
        selectedDiet={selectedDiet}
        searchQuery={searchQuery}
        onSelectDiet={setSelectedDiet}
        onBackToDiets={handleBackToDiets}
        onLogMeal={handleLogMeal}
        loggingMealId={loggingMeal}
      />

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default Diets;
