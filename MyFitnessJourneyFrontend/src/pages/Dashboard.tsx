import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { getCurrentUser, getProfilePictureUrl } from '../services/authService';
import { createMeal, getAllDiets, CreateMealRequest } from '../services/dietService';
import { logMeal } from '../services/mealLogService';
import { getUnreadCount } from '../services/chatService';
import { getCompletedWorkoutsCount } from '../services/fitnessProgramService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { Loading } from '../components/common/Loading';
import { DashboardHeader } from '../components/dashboard/DashboardHeader';
import { DashboardMain } from '../components/dashboard/DashboardMain';
import { CreateMealModal } from '../components/dashboard/CreateMealModal';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const { t } = useTranslation();
  const [username, setUsername] = useState<string>('User');
  const [pictureUrl, setPictureUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  
  const [calories, setCalories] = useState<number>(0);
  const [protein, setProtein] = useState<number>(0);
  const [carbs, setCarbs] = useState<number>(0);
  const [fat, setFat] = useState<number>(0);
  const [showMealModal, setShowMealModal] = useState<boolean>(false);
  const [isCreating, setIsCreating] = useState<boolean>(false);
  const [userFavoritesDietId, setUserFavoritesDietId] = useState<number | null>(null);
  const [unreadMessages, setUnreadMessages] = useState<number>(0);
  const [streak, setStreak] = useState<number>(0);
  const [workoutsCount, setWorkoutsCount] = useState<number>(0);
  
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
          setStreak(user.streak ?? 0);
          setPictureUrl(getProfilePictureUrl(user.pictureUrl));
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

  // Fetch unread message count
  useEffect(() => {
    const fetchUnreadMessages = async () => {
      try {
        const count = await getUnreadCount();
        setUnreadMessages(count);
      } catch (error) {
        console.error('Failed to fetch unread messages:', error);
      }
    };

    fetchUnreadMessages();

    // Refresh unread count periodically
    const interval = setInterval(fetchUnreadMessages, 30000);

    return () => clearInterval(interval);
  }, []);

  const fetchWorkoutsCount = async () => {
    try {
      const count = await getCompletedWorkoutsCount();
      setWorkoutsCount(count);
    } catch {
      setWorkoutsCount(0);
    }
  };

  useEffect(() => {
    fetchWorkoutsCount();
    const handleVisibilityChange = () => {
      if (!document.hidden) fetchWorkoutsCount();
    };
    window.addEventListener('focus', fetchWorkoutsCount);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => {
      window.removeEventListener('focus', fetchWorkoutsCount);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
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
      showSuccess(t('dashboard.mealCreated'));
      
      // Note: Navigation is handled by the modal or component that triggers this
    } catch (error) {
      console.error('Failed to create meal:', error);
      showError(t('dashboard.mealCreateError'));
    } finally {
      setIsCreating(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <Loading message={t('dashboard.loading')} />
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      <DashboardHeader username={username} pictureUrl={pictureUrl} />

      <DashboardMain
        calories={calories}
        protein={protein}
        carbs={carbs}
        fat={fat}
        targetCalories={targetCalories}
        targetProtein={targetProtein}
        targetCarbs={targetCarbs}
        targetFat={targetFat}
        unreadMessages={unreadMessages}
        streak={streak}
        workoutsCount={workoutsCount}
        onLogMeal={() => setShowMealModal(true)}
      />

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
