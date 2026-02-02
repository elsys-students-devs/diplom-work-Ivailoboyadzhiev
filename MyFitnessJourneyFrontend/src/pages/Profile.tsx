import React, { useState, useEffect, useRef } from 'react';
import {
  getCurrentUser,
  updateProfile,
  changePassword,
  uploadProfilePicture,
  getProfilePictureUrl,
  UserDto,
} from '../services/authService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { ProfileHeader } from '../components/profile/ProfileHeader';
import { Loading } from '../components/common/Loading';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './Profile.css';

const Profile: React.FC = () => {
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);

  const [editUsername, setEditUsername] = useState(false);
  const [editEmail, setEditEmail] = useState(false);
  const [usernameValue, setUsernameValue] = useState('');
  const [emailValue, setEmailValue] = useState('');

  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [changingPassword, setChangingPassword] = useState(false);

  const [savingProfile, setSavingProfile] = useState(false);
  const [uploadingPicture, setUploadingPicture] = useState(false);
  const [avatarLoadError, setAvatarLoadError] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const { toasts, showSuccess, showError, removeToast } = useToast();

  const fetchUser = async () => {
    try {
      const u = await getCurrentUser();
      setUser(u);
      if (u) {
        setUsernameValue(u.username ?? u.email?.split('@')[0] ?? '');
        setEmailValue(u.email ?? '');
      }
    } catch (error) {
      console.error('Failed to fetch user:', error);
      showError('Грешка при зареждане на профила');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  const handleSaveUsername = async () => {
    if (!user || savingProfile) return;
    const trimmed = usernameValue.trim();
    if (trimmed.length < 3) {
      showError('Потребителското име трябва да е поне 3 символа');
      return;
    }
    setSavingProfile(true);
    try {
      const updated = await updateProfile({ username: trimmed });
      setUser(updated);
      setEditUsername(false);
      showSuccess('Потребителското име е обновено');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Грешка при запазване';
      showError(message);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleSaveEmail = async () => {
    if (!user || savingProfile) return;
    const trimmed = emailValue.trim();
    if (!trimmed.includes('@')) {
      showError('Въведете валиден имейл');
      return;
    }
    setSavingProfile(true);
    try {
      const updated = await updateProfile({ email: trimmed });
      setUser(updated);
      setEditEmail(false);
      showSuccess('Имейлът е обновен');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Грешка при запазване';
      showError(message);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPassword.trim()) {
      showError('Въведете текущата парола');
      return;
    }
    if (newPassword.length < 6) {
      showError('Новата парола трябва да е поне 6 символа');
      return;
    }
    if (newPassword !== confirmPassword) {
      showError('Новата парола и потвърждението не съвпадат');
      return;
    }
    setChangingPassword(true);
    try {
      await changePassword({ currentPassword, newPassword });
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      showSuccess('Паролата е сменена успешно');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Грешка при смяна на паролата';
      showError(message);
    } finally {
      setChangingPassword(false);
    }
  };

  const handlePictureClick = () => fileInputRef.current?.click();

  const handlePictureChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !user) return;
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      showError('Изберете снимка (JPEG, PNG, GIF или WebP)');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      showError('Файлът трябва да е под 5 MB');
      return;
    }
    setUploadingPicture(true);
    setAvatarLoadError(false);
    try {
      const updated = await uploadProfilePicture(file);
      setUser(updated);
      showSuccess('Профилната снимка е обновена');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Грешка при качване';
      showError(message);
    } finally {
      setUploadingPicture(false);
      e.target.value = '';
    }
  };

  const pictureUrl = user ? getProfilePictureUrl(user.pictureUrl) : null;
  const hasPassword = user?.email != null; // OAuth-only users may not have password set on frontend; backend will reject change

  if (loading) {
    return (
      <div className="profile-container">
        <Loading message="Зареждане..." />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="profile-container">
        <ProfileHeader />
        <div className="profile-content">
          <p className="profile-error">Не сте влезли в системата.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      <ProfileHeader />

      <div className="profile-content">
        <section className="profile-card profile-picture-section">
          <div className="profile-avatar-wrap" onClick={handlePictureClick}>
            {uploadingPicture ? (
              <div className="profile-avatar profile-avatar-loading">...</div>
            ) : pictureUrl && !avatarLoadError ? (
              <img src={pictureUrl} alt="Профил" className="profile-avatar" onError={() => setAvatarLoadError(true)} />
            ) : (
              <div className="profile-avatar profile-avatar-placeholder">
                {(user.username || user.email)?.[0]?.toUpperCase() ?? '?'}
              </div>
            )}
            <span className="profile-avatar-overlay">Промяна</span>
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            className="profile-file-input"
            onChange={handlePictureChange}
          />
        </section>

        <section className="profile-card">
          <h2 className="profile-card-title">Потребителско име</h2>
          {editUsername ? (
            <div className="profile-edit-row">
              <input
                type="text"
                value={usernameValue}
                onChange={(e) => setUsernameValue(e.target.value)}
                className="profile-input"
                placeholder="Потребителско име"
                minLength={3}
                maxLength={50}
              />
              <button type="button" className="profile-btn profile-btn-primary" onClick={handleSaveUsername} disabled={savingProfile}>
                Запази
              </button>
              <button type="button" className="profile-btn profile-btn-secondary" onClick={() => { setEditUsername(false); setUsernameValue(user.username ?? ''); }}>
                Отказ
              </button>
            </div>
          ) : (
            <div className="profile-display-row">
              <span className="profile-value">{user.username || '—'}</span>
              <button type="button" className="profile-btn profile-btn-link" onClick={() => setEditUsername(true)}>Редактирай</button>
            </div>
          )}
        </section>

        <section className="profile-card">
          <h2 className="profile-card-title">Имейл</h2>
          {editEmail ? (
            <div className="profile-edit-row">
              <input
                type="email"
                value={emailValue}
                onChange={(e) => setEmailValue(e.target.value)}
                className="profile-input"
                placeholder="Имейл"
              />
              <button type="button" className="profile-btn profile-btn-primary" onClick={handleSaveEmail} disabled={savingProfile}>
                Запази
              </button>
              <button type="button" className="profile-btn profile-btn-secondary" onClick={() => { setEditEmail(false); setEmailValue(user.email ?? ''); }}>
                Отказ
              </button>
            </div>
          ) : (
            <div className="profile-display-row">
              <span className="profile-value">{user.email || '—'}</span>
              <button type="button" className="profile-btn profile-btn-link" onClick={() => setEditEmail(true)}>Редактирай</button>
            </div>
          )}
        </section>

        <section className="profile-card">
          <h2 className="profile-card-title">Парола</h2>
          <p className="profile-hint">За да смените паролата, въведете текущата парола и новата.</p>
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
            <button type="submit" className="profile-btn profile-btn-primary" disabled={changingPassword}>
              {changingPassword ? 'Зареждане...' : 'Смени паролата'}
            </button>
          </form>
        </section>
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default Profile;
