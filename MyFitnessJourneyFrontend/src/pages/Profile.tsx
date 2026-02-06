import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { getCurrentUser, updateProfile, UserDto } from '../services/authService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { ProfileHeader } from '../components/profile/ProfileHeader';
import { ProfilePictureSection } from '../components/profile/ProfilePictureSection';
import { ProfilePasswordSection } from '../components/profile/ProfilePasswordSection';
import { FieldEdited } from '../components/common/FieldEdited/FieldEdited';
import { Loading } from '../components/common/Loading';
import { ToastContainer } from '../components/common/Toast';
import { useToast } from '../components/common/useToast';
import './Profile.css';

const Profile: React.FC = () => {
  const { t } = useTranslation();
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);

  const [editUsername, setEditUsername] = useState(false);
  const [editEmail, setEditEmail] = useState(false);
  const [usernameValue, setUsernameValue] = useState('');
  const [emailValue, setEmailValue] = useState('');
  const [savingProfile, setSavingProfile] = useState(false);

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
      showError(t('profile.saveError'));
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
      showError(t('profile.usernameMinError'));
      return;
    }
    setSavingProfile(true);
    try {
      const updated = await updateProfile({ username: trimmed });
      setUser(updated);
      setEditUsername(false);
      showSuccess(t('profile.usernameUpdated'));
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        t('profile.saveError');
      showError(message);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleSaveEmail = async () => {
    if (!user || savingProfile) return;
    const trimmed = emailValue.trim();
    if (!trimmed.includes('@')) {
      showError(t('profile.validEmailError'));
      return;
    }
    setSavingProfile(true);
    try {
      const updated = await updateProfile({ email: trimmed });
      setUser(updated);
      setEditEmail(false);
      showSuccess(t('profile.emailUpdated'));
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        t('profile.saveError');
      showError(message);
    } finally {
      setSavingProfile(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-container">
        <Loading message={t('profile.loading')} />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="profile-container">
        <ProfileHeader />
        <div className="profile-content">
          <p className="profile-error">{t('profile.notLoggedIn')}</p>
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
        <ProfilePictureSection
          user={user}
          onUserUpdate={setUser}
          onSuccess={showSuccess}
          onError={showError}
        />

        <FieldEdited
          title={t('profile.username')}
          displayValue={user.username || '—'}
          value={usernameValue}
          onChange={setUsernameValue}
          onSave={handleSaveUsername}
          onCancel={() => {
            setEditUsername(false);
            setUsernameValue(user.username ?? '');
          }}
          onStartEdit={() => setEditUsername(true)}
          isEditing={editUsername}
          saving={savingProfile}
          inputPlaceholder={t('profile.usernamePlaceholder')}
          inputMinLength={3}
          inputMaxLength={50}
        />

        <FieldEdited
          title={t('profile.email')}
          displayValue={user.email || '—'}
          value={emailValue}
          onChange={setEmailValue}
          onSave={handleSaveEmail}
          onCancel={() => {
            setEditEmail(false);
            setEmailValue(user.email ?? '');
          }}
          onStartEdit={() => setEditEmail(true)}
          isEditing={editEmail}
          saving={savingProfile}
          inputType="email"
          inputPlaceholder={t('profile.emailPlaceholder')}
        />

        <ProfilePasswordSection onSuccess={showSuccess} onError={showError} />
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  );
};

export default Profile;
