import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser } from '../services/authService';
import { createMeal, getAllDiets, CreateMealRequest } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { Loading } from '../components/common/Loading';
import { SummaryCards } from '../components/dashboard/SummaryCards';
import { NutritionSummary } from '../components/dashboard/NutritionSummary';
import { CreateMealModal } from '../components/dashboard/CreateMealModal';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
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
  const [isCreating, setIsCreating] = useState<boolean>(false);
  const [userFavoritesDietId, setUserFavoritesDietId] = useState<number | null>(null);
  
  const { toasts, showSuccess, showError, removeToast } = useToast();
  
  // Target values
  const targetCalories = 2000;
  const targetProtein = 150;
  const targetCarbs = 250;
  const targetFat = 65;

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const user = await getCurrentUser();
        
        if (user) {
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

  // Fetch User Favorites diet ID
  useEffect(() => {
    const fetchUserFavoritesDiet = async () => {
      try {
        const diets = await getAllDiets();
        const userFavorites = diets.find(d => d.name === 'User Favorites');
        if (userFavorites) {
          setUserFavoritesDietId(userFavorites.id);
        }
      } catch (error) {
        console.error('Failed to fetch User Favorites diet:', error);
      }
    };

    fetchUserFavoritesDiet();
  }, []);

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
    
    const handleVisibilityChange = () => {
      if (!document.hidden) {
        fetchNutritionSummary();
      }
    };
    
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

  const handleCreateMeal = async (mealForm: CreateMealRequest) => {
    setIsCreating(true);
    try {
      const createdMeal = await createMeal(mealForm);
      await logMeal(createdMeal.id);
      await fetchNutritionSummary();
      
      setShowMealModal(false);
      showSuccess('Ястието е създадено и логнато успешно!');
      
      // Navigate to User Favorites diet if we have the ID
      if (userFavoritesDietId) {
        navigate(`/diets?dietId=${userFavoritesDietId}`);
      } else {
        navigate('/diets');
      }
    } catch (error) {
      console.error('Failed to create meal:', error);
      showError('Грешка при създаване на ястието. Моля опитайте отново.');
    } finally {
      setIsCreating(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <Loading message="Loading..." />
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

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
        <SummaryCards />
        
        <NutritionSummary
          calories={calories}
          protein={protein}
          carbs={carbs}
          fat={fat}
          targetCalories={targetCalories}
          targetProtein={targetProtein}
          targetCarbs={targetCarbs}
          targetFat={targetFat}
          onLogMeal={() => setShowMealModal(true)}
        />

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

      <CreateMealModal
        isOpen={showMealModal}
        onClose={() => setShowMealModal(false)}
        onSubmit={handleCreateMeal}
        isCreating={isCreating}
      />

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default Dashboard;
