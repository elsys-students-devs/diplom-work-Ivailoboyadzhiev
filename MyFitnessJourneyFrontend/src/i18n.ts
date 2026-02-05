import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import appBg from './locales/bg/app.json';
import loginBg from './locales/bg/login.json';
import registerBg from './locales/bg/register.json';
import menuBg from './locales/bg/menu.json';
import dashboardBg from './locales/bg/dashboard.json';
import profileBg from './locales/bg/profile.json';
import chatBg from './locales/bg/chat.json';
import dietsBg from './locales/bg/diets.json';
import fitnessBg from './locales/bg/fitness.json';
import commonBg from './locales/bg/common.json';

import appEn from './locales/en/app.json';
import loginEn from './locales/en/login.json';
import registerEn from './locales/en/register.json';
import menuEn from './locales/en/menu.json';
import dashboardEn from './locales/en/dashboard.json';
import profileEn from './locales/en/profile.json';
import chatEn from './locales/en/chat.json';
import dietsEn from './locales/en/diets.json';
import fitnessEn from './locales/en/fitness.json';
import commonEn from './locales/en/common.json';

const STORAGE_KEY = 'app-locale';

function getDefaultLanguage(): string {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved === 'en' || saved === 'bg') return saved;
  const browserLang = navigator.language || (navigator as { userLanguage?: string }).userLanguage || '';
  if (browserLang.toLowerCase().startsWith('bg')) return 'bg';
  return 'en';
}

function mergeTranslations(
  ...modules: Record<string, unknown>[]
): Record<string, unknown> {
  return Object.assign({}, ...modules);
}

const bg = mergeTranslations(
  appBg,
  loginBg,
  registerBg,
  menuBg,
  dashboardBg,
  profileBg,
  chatBg,
  dietsBg,
  fitnessBg,
  commonBg
);

const en = mergeTranslations(
  appEn,
  loginEn,
  registerEn,
  menuEn,
  dashboardEn,
  profileEn,
  chatEn,
  dietsEn,
  fitnessEn,
  commonEn
);

const initialLang = getDefaultLanguage();
localStorage.setItem(STORAGE_KEY, initialLang);

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    bg: { translation: bg },
  },
  lng: initialLang,
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

i18n.on('languageChanged', (lng) => {
  localStorage.setItem(STORAGE_KEY, lng);
});

export default i18n;
export { STORAGE_KEY };
