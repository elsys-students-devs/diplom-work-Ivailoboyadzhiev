import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser } from '../services/authService';
import { createMeal, CreateMealRequest } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string>('User');
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  
  const [calories, setCalories] = useState<number>(0);
  const [protein, setProtein] = useState<number>(0);
  const [carbs, setCarbs] = useState<number>(0);
  const [fat, setFat] = useState<number>(0);
  const [showMealModal, setShowMealModal] = useState<boolean>(false);
  const [mealForm, setMealForm] = useState<CreateMealRequest>({
    name: '',
    description: '',
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0,
    fiber: 0,
    sugar: 0
  });
  const [isCreating, setIsCreating] = useState<boolean>(false);
  
  // Target values (change this to target values from the workout plan)
  const targetCalories = 2000;
  const targetProtein = 150; // grams
  const targetCarbs = 250; // grams
  const targetFat = 65; // grams
  
  // Calculate circle circumference (2 * π * radius) radius is 45
  const circumference = 2 * Math.PI * 45; 
  
  // Calculate percentage and strokeDasharray for each circle
  const getCircleProgress = (current: number, target: number): string => {
    const percentage = Math.min(current / target, 1); // Cap at 100%
    const offset = circumference * (1 - percentage);
    return `${circumference * percentage} ${circumference}`;
  };

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const user = await getCurrentUser();
        
        if (user) {
          // Priority: username > name > email prefix
          const displayName = (user.username && user.username.trim()) || 
                             (user.name && user.name.trim()) || 
                             (user.email ? user.email.split('@')[0] : 'User');
          setUsername(displayName);
        }
      } catch (error) {
        console.error('Failed to fetch user:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

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

  // Fetch today's nutrition summary
  const fetchNutritionSummary = async () => {
    try {
      const { getTodayNutritionSummary } = await import('../services/mealLogService');
      const summary = await getTodayNutritionSummary();
      setCalories(Math.round(summary.totalCalories || 0));
      setProtein(Math.round(summary.totalProtein || 0));
      setCarbs(Math.round(summary.totalCarbs || 0));
      setFat(Math.round(summary.totalFat || 0));
    } catch (error) {
      console.error('Failed to fetch nutrition summary:', error);
    }
  };

  useEffect(() => {
    fetchNutritionSummary();
    
    // Refresh nutrition summary when page becomes visible (user returns from diets page)
    const handleVisibilityChange = () => {
      if (!document.hidden) {
        fetchNutritionSummary();
      }
    };
    
    // Refresh when window gets focus (user switches back to tab)
    const handleFocus = () => {
      fetchNutritionSummary();
    };
    
    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('focus', handleFocus);
    
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      window.removeEventListener('focus', handleFocus);
    };
  }, []);

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
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
          <div className="dropdown-menu-item">Profile</div>
          <div className="dropdown-menu-item">Settings</div>
          <div className="dropdown-menu-item">Help</div>
          <div className="dropdown-menu-item">Logout</div>
        </div>
      )}

      {/* Header Section */}
      <div className="dashboard-header">
        <div className="header-content">
          <p className="welcome-text">Welcome back</p>
          <h1 className="user-name">{username}</h1>
          <p className="motivational-text">Let's crush your fitness goals today!</p>
        </div>
      </div>

      {/* Main Content */}
      <div className="dashboard-content">
        {/* Summary Cards */}
        <div className="summary-cards">
          <div className="summary-card">
            <div className="card-header">
              <span className="card-title">WORKOUTS</span>
              <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="card-value">0</div>
            <div className="card-subtitle">completed</div>
          </div>

          <div className="summary-card">
            <div className="card-header">
              <span className="card-title">MESSAGES</span>
              <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="card-value">0</div>
            <div className="card-subtitle">unread</div>
          </div>

          <div className="summary-card">
            <div className="card-header">
              <span className="card-title">STREAK</span>
              <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="card-value">7</div>
            <div className="card-subtitle">days</div>
          </div>
        </div>

        {/* Today's Nutrition Section */}
        <div className="nutrition-section">
          <div className="nutrition-header">
            <h2 className="nutrition-title">Today's Nutrition</h2>
            <button className="log-meal-btn" onClick={() => setShowMealModal(true)}>Log Meal</button>
          </div>
          <div className="nutrition-circles">
            <div className="nutrition-circle">
              <svg className="circle-progress" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" stroke="#999" strokeWidth="8" fill="none"/>
                <circle cx="50" cy="50" r="45" stroke="#FF5722" strokeWidth="8" fill="none" 
                  strokeDasharray={getCircleProgress(calories, targetCalories)} 
                  strokeLinecap="round" transform="rotate(-90 50 50)"/>
              </svg>
              <div className="circle-value">{calories}</div>
              <div className="circle-label">CALORIES</div>
            </div>
            <div className="nutrition-circle">
              <svg className="circle-progress" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" stroke="#999" strokeWidth="8" fill="none"/>
                <circle cx="50" cy="50" r="45" stroke="#FF5722" strokeWidth="8" fill="none" 
                  strokeDasharray={getCircleProgress(protein, targetProtein)} 
                  strokeLinecap="round" transform="rotate(-90 50 50)"/>
              </svg>
              <div className="circle-value">{protein}</div>
              <div className="circle-label">PROTEIN</div>
            </div>
            <div className="nutrition-circle">
              <svg className="circle-progress" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" stroke="#999" strokeWidth="8" fill="none"/>
                <circle cx="50" cy="50" r="45" stroke="#FF5722" strokeWidth="8" fill="none" 
                  strokeDasharray={getCircleProgress(carbs, targetCarbs)} 
                  strokeLinecap="round" transform="rotate(-90 50 50)"/>
              </svg>
              <div className="circle-value">{carbs}</div>
              <div className="circle-label">CARBS</div>
            </div>
            <div className="nutrition-circle">
              <svg className="circle-progress" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" stroke="#999" strokeWidth="8" fill="none"/>
                <circle cx="50" cy="50" r="45" stroke="#FF5722" strokeWidth="8" fill="none" 
                  strokeDasharray={getCircleProgress(fat, targetFat)} 
                  strokeLinecap="round" transform="rotate(-90 50 50)"/>
              </svg>
              <div className="circle-value">{fat}</div>
              <div className="circle-label">FAT</div>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="action-buttons">
          <button className="action-button diet-plans" onClick={() => navigate('/diets')}>
            <div className="button-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M6 2L3 6v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6l-3-4H6zM3 6h18M8 10v4M12 10v4M16 10v4" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="button-content">
              <div className="button-title">Diet Plans</div>
              <div className="button-subtitle">View meal plans & nutrition</div>
            </div>
            <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>

          <button className="action-button fitness-programs">
            <div className="button-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="button-content">
              <div className="button-title">Fitness Programs</div>
              <div className="button-subtitle">Workouts & training</div>
            </div>
            <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>

          <button className="action-button messages">
            <div className="button-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="button-content">
              <div className="button-title">Messages</div>
              <div className="button-subtitle">Chat with trainer</div>
            </div>
            <svg className="button-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M5 12h14M12 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>
        </div>
      </div>

      {/* Create Meal Modal */}
      {showMealModal && (
        <div className="modal-overlay" onClick={() => setShowMealModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Създай ново ястие</h2>
              <button className="modal-close" onClick={() => setShowMealModal(false)}>×</button>
            </div>
            <form className="meal-form" onSubmit={handleCreateMeal}>
              <div className="form-group">
                <label>Име на ястието *</label>
                <input
                  type="text"
                  value={mealForm.name}
                  onChange={(e) => setMealForm({...mealForm, name: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Описание</label>
                <textarea
                  value={mealForm.description}
                  onChange={(e) => setMealForm({...mealForm, description: e.target.value})}
                  rows={3}
                />
              </div>
              <div className="macros-grid">
                <div className="form-group">
                  <label>Калории *</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.calories || ''}
                    onChange={(e) => setMealForm({...mealForm, calories: parseFloat(e.target.value) || 0})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Протеин (g) *</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.protein || ''}
                    onChange={(e) => setMealForm({...mealForm, protein: parseFloat(e.target.value) || 0})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Въглехидрати (g) *</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.carbs || ''}
                    onChange={(e) => setMealForm({...mealForm, carbs: parseFloat(e.target.value) || 0})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Мазнини (g) *</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.fat || ''}
                    onChange={(e) => setMealForm({...mealForm, fat: parseFloat(e.target.value) || 0})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Фибри (g)</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.fiber || ''}
                    onChange={(e) => setMealForm({...mealForm, fiber: parseFloat(e.target.value) || 0})}
                  />
                </div>
                <div className="form-group">
                  <label>Захар (g)</label>
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.sugar || ''}
                    onChange={(e) => setMealForm({...mealForm, sugar: parseFloat(e.target.value) || 0})}
                  />
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowMealModal(false)}>
                  Отказ
                </button>
                <button type="submit" className="btn-submit" disabled={isCreating}>
                  {isCreating ? 'Създаване...' : 'Създай и логни'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );

  async function handleCreateMeal(e: React.FormEvent) {
    e.preventDefault();
    setIsCreating(true);
    try {
      // Create the meal
      const createdMeal = await createMeal(mealForm);
      
      // Log the meal
      await logMeal(createdMeal.id);
      
      // Refresh nutrition summary
      await fetchNutritionSummary();
      
      // Close modal and navigate to diets page showing User Favorites
      setShowMealModal(false);
      navigate('/diets?diet=User Favorites');
    } catch (error) {
      console.error('Failed to create meal:', error);
      alert('Грешка при създаване на ястието. Моля опитайте отново.');
    } finally {
      setIsCreating(false);
    }
  }
};

export default Dashboard;

