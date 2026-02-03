import React from 'react';
import { changePassword } from '../../../services/authService';

export interface ProfilePasswordSectionProps {
  onSuccess: (message: string) => void;
  onError: (message: string) => void;
}

export const ProfilePasswordSection: React.FC<ProfilePasswordSectionProps> = ({
  onSuccess,
  onError,
}) => {
  const [currentPassword, setCurrentPassword] = React.useState('');
  const [newPassword, setNewPassword] = React.useState('');
  const [confirmPassword, setConfirmPassword] = React.useState('');
  const [changingPassword, setChangingPassword] = React.useState(false);

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPassword.trim()) {
      onError('Въведете текущата парола');
      return;
    }
    if (newPassword.length < 6) {
      onError('Новата парола трябва да е поне 6 символа');
      return;
    }
    if (newPassword !== confirmPassword) {
      onError('Новата парола и потвърждението не съвпадат');
      return;
    }
    setChangingPassword(true);
    try {
      await changePassword({ currentPassword, newPassword });
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      onSuccess('Паролата е сменена успешно');
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Грешка при смяна на паролата';
      onError(message);
    } finally {
      setChangingPassword(false);
    }
  };

  return (
    <section className="profile-card">
      <h2 className="profile-card-title">Парола</h2>
      <p className="profile-hint">
        За да смените паролата, въведете текущата парола и новата.
      </p>
      <form onSubmit={handleChangePassword} className="profile-password-form">
        <input
          type="password"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
          className="profile-input"
          placeholder="Текуща парола"
          autoComplete="current-password"
        />
        <input
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          className="profile-input"
          placeholder="Нова парола"
          autoComplete="new-password"
          minLength={6}
        />
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="profile-input"
          placeholder="Потвърдете новата парола"
          autoComplete="new-password"
        />
        <button
          type="submit"
          className="profile-btn profile-btn-primary"
          disabled={changingPassword}
        >
          {changingPassword ? 'Зареждане...' : 'Смени паролата'}
        </button>
      </form>
    </section>
  );
};
