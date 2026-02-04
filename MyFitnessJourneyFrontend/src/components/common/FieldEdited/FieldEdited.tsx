import React from 'react';
import './FieldEdited.css';

export interface FieldEditedProps {
  title: string;
  displayValue: string;
  value: string;
  onChange: (value: string) => void;
  onSave: () => void;
  onCancel: () => void;
  onStartEdit: () => void;
  isEditing: boolean;
  saving: boolean;
  inputType?: 'text' | 'email';
  inputPlaceholder?: string;
  inputMinLength?: number;
  inputMaxLength?: number;
}

export const FieldEdited: React.FC<FieldEditedProps> = ({
  title,
  displayValue,
  value,
  onChange,
  onSave,
  onCancel,
  onStartEdit,
  isEditing,
  saving,
  inputType = 'text',
  inputPlaceholder = '',
  inputMinLength,
  inputMaxLength,
}) => {
  return (
    <section className="profile-card">
      <h2 className="profile-card-title">{title}</h2>
      {isEditing ? (
        <div className="profile-edit-row">
          <input
            type={inputType}
            value={value}
            onChange={(e) => onChange(e.target.value)}
            className="profile-input"
            placeholder={inputPlaceholder}
            minLength={inputMinLength}
            maxLength={inputMaxLength}
          />
          <button
            type="button"
            className="profile-btn profile-btn-primary"
            onClick={onSave}
            disabled={saving}
          >
            Запази
          </button>
          <button
            type="button"
            className="profile-btn profile-btn-secondary"
            onClick={onCancel}
          >
            Отказ
          </button>
        </div>
      ) : (
        <div className="profile-display-row">
          <span className="profile-value">{displayValue || '—'}</span>
          <button
            type="button"
            className="profile-btn profile-btn-link"
            onClick={onStartEdit}
          >
            Редактирай
          </button>
        </div>
      )}
    </section>
  );
};
