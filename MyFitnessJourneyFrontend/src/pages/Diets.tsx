import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getAllDiets, DietDto, MealDto } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import './Diets.css';

const Diets: React.FC = () => {
  const [diets, setDiets] = useState<DietDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDiet, setSelectedDiet] = useState<DietDto | null>(null);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [loggingMeal, setLoggingMeal] = useState<number | null>(null);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const fetchDiets = async () => {
      try {
        const data = await getAllDiets();
        setDiets(data);
      } catch (error) {
        console.error('Failed to fetch diets:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDiets();
  }, []);

  // Check if we should show specific diet or meals for logging
  useEffect(() => {
    const dietParam = searchParams.get('diet');
    const logMealParam = searchParams.get('logMeal');
    
    if (dietParam && diets.length > 0 && !selectedDiet) {
      // Find and select the diet by name
      const diet = diets.find(d => d.name === dietParam);
      if (diet) {
        setSelectedDiet(diet);
      }
    } else if (logMealParam === 'true' && diets.length > 0 && !selectedDiet) {
      // Auto-select first diet if coming from dashboard to log meal
      setSelectedDiet(diets[0]);
    }
  }, [searchParams, diets, selectedDiet]);

  const handleLogMeal = async (mealId: number) => {
    setLoggingMeal(mealId);
    try {
      await logMeal(mealId);
      // Show success message and stay on page so user can log more meals
      alert('Ястието е логнато успешно! Кръговете в Dashboard са обновени.');
      setLoggingMeal(null);
    } catch (error) {
      console.error('Failed to log meal:', error);
      alert('Грешка при логване на ястието. Моля опитайте отново.');
      setLoggingMeal(null);
    }
  };

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (menuOpen && !target.closest('.hamburger-menu-btn') && !target.closest('.dropdown-menu')) {
        setMenuOpen(false);
      }
    };

    if (menuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [menuOpen]);

  if (loading) {
    return (
      <div className="diets-container">
        <div className="loading">Зареждане...</div>
      </div>
    );
  }

  return (
    <div className="diets-container">
      {/* Hamburger Menu Button */}
      <button 
        className="hamburger-menu-btn" 
        onClick={() => setMenuOpen(!menuOpen)}
        aria-label="Toggle menu"
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <line x1="3" y1="6" x2="21" y2="6"></line>
          <line x1="3" y1="12" x2="21" y2="12"></line>
          <line x1="3" y1="18" x2="21" y2="18"></line>
        </svg>
      </button>

      {/* Dropdown Menu */}
      {menuOpen && (
        <div className="dropdown-menu">
          <div className="dropdown-menu-item" onClick={() => navigate('/dashboard')}>Dashboard</div>
          <div className="dropdown-menu-item">Profile</div>
          <div className="dropdown-menu-item">Settings</div>
          <div className="dropdown-menu-item">Help</div>
          <div className="dropdown-menu-item">Logout</div>
        </div>
      )}

      {/* Header Section */}
      <div className="diets-header">
        <button className="back-button" onClick={() => navigate('/dashboard')}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
        </button>
        <h1 className="diets-title">Диети и Хранителни Планове</h1>
        <p className="diets-subtitle">Изберете диета, която отговаря на вашите цели</p>
      </div>

      {/* Main Content */}
      <div className="diets-content">
        {!selectedDiet ? (
          /* Diet Cards Grid */
          <div className="diets-grid">
            {diets.map((diet) => (
              <div 
                key={diet.id} 
                className="diet-card"
                onClick={() => setSelectedDiet(diet)}
              >
                <div className="diet-card-header">
                  <h2 className="diet-card-title">{diet.name}</h2>
                </div>
                <div className="diet-card-body">
                  <p className="diet-card-description">{diet.description}</p>
                  {diet.benefits && (
                    <div className="diet-card-benefits">
                      <strong>Предимства:</strong>
                      <p>{diet.benefits}</p>
                    </div>
                  )}
                  <div className="diet-card-meals-count">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M6 2L3 6v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6l-3-4H6zM3 6h18M8 10v4M12 10v4M16 10v4"/>
                    </svg>
                    <span>{diet.meals?.length || 0} ястия</span>
                  </div>
                </div>
                <div className="diet-card-footer">
                  <span className="view-details">Виж детайли →</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          /* Meal Details View */
          <div className="meals-view">
            <button className="back-to-diets" onClick={() => setSelectedDiet(null)}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              Назад към диети
            </button>
            <div className="selected-diet-info">
              <h2 className="selected-diet-title">{selectedDiet.name}</h2>
              <p className="selected-diet-description">{selectedDiet.description}</p>
              {selectedDiet.benefits && (
                <div className="selected-diet-benefits">
                  <strong>Предимства:</strong> {selectedDiet.benefits}
                </div>
              )}
            </div>
            <div className="meals-grid">
              {selectedDiet.meals?.map((meal) => (
                <div key={meal.id} className="meal-card">
                  <div className="meal-card-header">
                    <h3 className="meal-card-title">{meal.name}</h3>
                  </div>
                  <div className="meal-card-body">
                    {meal.description && (
                      <p className="meal-card-description">{meal.description}</p>
                    )}
                    <div className="meal-macros">
                      <div className="macro-item">
                        <span className="macro-label">Калории</span>
                        <span className="macro-value">{meal.calories} kcal</span>
                      </div>
                      <div className="macro-item">
                        <span className="macro-label">Протеин</span>
                        <span className="macro-value">{meal.protein}g</span>
                      </div>
                      <div className="macro-item">
                        <span className="macro-label">Въглехидрати</span>
                        <span className="macro-value">{meal.carbs}g</span>
                      </div>
                      <div className="macro-item">
                        <span className="macro-label">Мазнини</span>
                        <span className="macro-value">{meal.fat}g</span>
                      </div>
                      {meal.fiber && (
                        <div className="macro-item">
                          <span className="macro-label">Фибри</span>
                          <span className="macro-value">{meal.fiber}g</span>
                        </div>
                      )}
                      {meal.sugar && (
                        <div className="macro-item">
                          <span className="macro-label">Захар</span>
                          <span className="macro-value">{meal.sugar}g</span>
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="meal-card-footer">
                    <button 
                      className="log-meal-button"
                      onClick={() => handleLogMeal(meal.id)}
                      disabled={loggingMeal === meal.id}
                    >
                      {loggingMeal === meal.id ? 'Логване...' : 'Логни ястие'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Diets;

