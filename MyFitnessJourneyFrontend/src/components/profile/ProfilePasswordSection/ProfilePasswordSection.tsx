import React from 'react';
import { useTranslation } from 'react-i18next';
import { changePassword } from '../../../services/authService';

export interface ProfilePasswordSectionProps {
  onSuccess: (message: string) => void;
  onError: (message: string) => void;
}

export const ProfilePasswordSection: React.FC<ProfilePasswordSectionProps> = ({
  onSuccess,
  onError,
}) => {
  const { t } = useTranslation();
  const [currentPassword, setCurrentPassword] = React.useState('');
  const [newPassword, setNewPassword] = React.useState('');
  const [confirmPassword, setConfirmPassword] = React.useState('');
  const [changingPassword, setChangingPassword] = React.useState(false);

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPassword.trim()) {
      onError(t('profile.currentPasswordError'));
      return;
    }
    if (newPassword.length < 6) {
      onError(t('profile.newPasswordMinError'));
      return;
    }
    if (newPassword !== confirmPassword) {
      onError(t('profile.passwordMismatch'));
      return;
    }
    setChangingPassword(true);
    try {
      await changePassword({ currentPassword, newPassword });
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      onSuccess(t('profile.passwordChanged'));
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        t('profile.passwordChangeError');
      onError(message);
    } finally {
      setChangingPassword(false);
    }
  };

  return (
    <section className="profile-card">
      <h2 className="profile-card-title">{t('profile.password')}</h2>
      <p className="profile-hint">
        {t('profile.passwordHint')}
      </p>
      <form onSubmit={handleChangePassword} className="profile-password-form">
        <input
          type="password"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
          className="profile-input"
          placeholder={t('profile.currentPassword')}
          autoComplete="current-password"
        />
        <input
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          className="profile-input"
          placeholder={t('profile.newPassword')}
          autoComplete="new-password"
          minLength={6}
        />
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="profile-input"
          placeholder={t('profile.confirmPassword')}
          autoComplete="new-password"
        />
        <button
          type="submit"
          className="profile-btn profile-btn-primary"
          disabled={changingPassword}
        >
          {changingPassword ? t('profile.changing') : t('profile.changePassword')}
        </button>
      </form>
    </section>
  );
};
