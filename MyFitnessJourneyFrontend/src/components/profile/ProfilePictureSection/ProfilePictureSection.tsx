import React, { useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { UserDto, getProfilePictureUrl, uploadProfilePicture } from '../../../services/authService';

export interface ProfilePictureSectionProps {
  user: UserDto;
  onUserUpdate: (user: UserDto) => void;
  onSuccess: (message: string) => void;
  onError: (message: string) => void;
}

export const ProfilePictureSection: React.FC<ProfilePictureSectionProps> = ({
  user,
  onUserUpdate,
  onSuccess,
  onError,
}) => {
  const { t } = useTranslation();
  const [uploadingPicture, setUploadingPicture] = React.useState(false);
  const [avatarLoadError, setAvatarLoadError] = React.useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const pictureUrl = getProfilePictureUrl(user.pictureUrl);

  const handlePictureClick = () => fileInputRef.current?.click();

  const handlePictureChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      onError(t('profile.pictureErrorType'));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      onError(t('profile.pictureErrorSize'));
      return;
    }
    setUploadingPicture(true);
    setAvatarLoadError(false);
    try {
      const updated = await uploadProfilePicture(file);
      onUserUpdate(updated);
      onSuccess(t('profile.pictureUpdated'));
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        t('profile.uploadError');
      onError(message);
    } finally {
      setUploadingPicture(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  return (
    <section className="profile-card profile-picture-section">
      <div className="profile-avatar-wrap" onClick={handlePictureClick}>
        {uploadingPicture ? (
          <div className="profile-avatar profile-avatar-loading">...</div>
        ) : pictureUrl && !avatarLoadError ? (
          <img
            src={pictureUrl}
            alt={t('profile.profilePicture')}
            className="profile-avatar"
            onError={() => setAvatarLoadError(true)}
          />
        ) : (
          <div className="profile-avatar profile-avatar-placeholder">
            {(user.username || user.email)?.[0]?.toUpperCase() ?? '?'}
          </div>
        )}
        <span className="profile-avatar-overlay">{t('profile.changePicture')}</span>
      </div>
      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/gif,image/webp"
        className="profile-file-input"
        onChange={handlePictureChange}
      />
    </section>
  );
};
