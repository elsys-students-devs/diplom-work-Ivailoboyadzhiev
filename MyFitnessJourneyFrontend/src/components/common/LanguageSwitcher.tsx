import React from 'react';
import { useTranslation } from 'react-i18next';
import './LanguageSwitcher.css';

export const LanguageSwitcher: React.FC = () => {
  const { i18n, t } = useTranslation();

  const toggleLanguage = () => {
    const next = i18n.language === 'bg' ? 'en' : 'bg';
    i18n.changeLanguage(next);
  };

  const currentLabel = i18n.language === 'bg' ? t('language.bg') : t('language.en');
  const otherLabel = i18n.language === 'bg' ? t('language.en') : t('language.bg');

  return (
    <button
      type="button"
      className="language-switcher"
      onClick={toggleLanguage}
      title={t('language.switchTo')}
      aria-label={t('language.switchTo')}
    >
      <span className="language-switcher-current">{currentLabel}</span>
      <span className="language-switcher-sep">/</span>
      <span className="language-switcher-other">{otherLabel}</span>
    </button>
  );
};
