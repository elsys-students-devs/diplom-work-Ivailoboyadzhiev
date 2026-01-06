import React from 'react';
import { CreateMealRequest } from '../../services/dietService';
import './CreateMealModal.css';

interface CreateMealModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (meal: CreateMealRequest) => Promise<void>;
  isCreating?: boolean;
}

export const CreateMealModal: React.FC<CreateMealModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  isCreating = false,
}) => {
  const [mealForm, setMealForm] = React.useState<CreateMealRequest>({
    name: '',
    description: '',
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0,
    fiber: 0,
    sugar: 0,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit(mealForm);
    // Reset form after successful submission
    setMealForm({
      name: '',
      description: '',
      calories: 0,
      protein: 0,
      carbs: 0,
      fat: 0,
      fiber: 0,
      sugar: 0,
    });
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Създай ново ястие</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form className="meal-form" onSubmit={handleSubmit}>
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
            <button type="button" className="btn-cancel" onClick={onClose}>
              Отказ
            </button>
            <button type="submit" className="btn-submit" disabled={isCreating}>
              {isCreating ? 'Създаване...' : 'Създай и логни'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

