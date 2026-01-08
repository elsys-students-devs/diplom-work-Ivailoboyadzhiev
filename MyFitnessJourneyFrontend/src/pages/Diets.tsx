import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getAllDiets, getDietById, DietDto } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { SearchBar } from '../components/common/SearchBar';
import { Loading } from '../components/common/Loading';
import { DietGrid } from '../components/diets/DietGrid';
import { SelectedDietHeader } from '../components/diets/SelectedDietHeader';
import { MealsGrid } from '../components/diets/MealsGrid';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './Diets.css';

const Diets: React.FC = () => {
  const [diets, setDiets] = useState<DietDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDiet, setSelectedDiet] = useState<DietDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [loggingMeal, setLoggingMeal] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { toasts, showSuccess, showError, removeToast } = useToast();

  useEffect(() => {
    const fetchDiets = async () => {
      try {
        const data = await getAllDiets();
        setDiets(data);
      } catch (error) {
        console.error('Failed to fetch diets:', error);
        showError('Грешка при зареждане на диетите');
      } finally {
        setLoading(false);
      }
    };

    fetchDiets();
  }, [showError]);

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
            .catch(() => showError('Диетата не беше намерена'));
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
      showSuccess('Ястието е логнато успешно! Кръговете в Dashboard са обновени.');
      setLoggingMeal(null);
    } catch (error) {
      console.error('Failed to log meal:', error);
      showError('Грешка при логване на ястието. Моля опитайте отново.');
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
        <Loading message="Зареждане..." />
      </div>
    );
  }

  return (
    <div className="diets-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      {/* Header Section */}
      <div className="diets-header">
        <button className="back-button" onClick={() => navigate('/dashboard')}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
        </button>
        <h1 className="diets-title">Диети и Хранителни Планове</h1>
        <p className="diets-subtitle">Изберете диета, която отговаря на вашите цели</p>
        
        <SearchBar
          value={searchQuery}
          onChange={setSearchQuery}
          placeholder={selectedDiet ? 'Търси ястие...' : 'Търси диета...'}
        />
      </div>

      {/* Main Content */}
      <div className="diets-content">
        {!selectedDiet ? (
          <DietGrid
            diets={diets}
            search={searchQuery}
            onSelect={setSelectedDiet}
          />
        ) : (
          <div className="meals-view">
            <SelectedDietHeader
              diet={selectedDiet}
              onBack={handleBackToDiets}
            />
            <MealsGrid
              meals={selectedDiet.meals ?? []}
              search={searchQuery}
              onLogMeal={handleLogMeal}
              loggingMealId={loggingMeal}
            />
          </div>
        )}
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default Diets;
