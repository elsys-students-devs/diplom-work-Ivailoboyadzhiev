import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import './Login.css';

const OAuth2Callback: React.FC = () => {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const error = searchParams.get('error');
    const userId = searchParams.get('userId');
    const email = searchParams.get('email');

    if (error) {
      // Redirect to login with error
      navigate(`/login?error=${encodeURIComponent(error)}`);
      return;
    }

    if (userId && email) {
      // OAuth2 login successful, session is automatically created
      // Redirect to dashboard
      navigate('/dashboard', { replace: true });
    } else {
      // Missing parameters, redirect to login
      navigate('/login?error=oauth2_failed');
    }
  }, [searchParams, navigate]);

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>{t('oauth.title')}</h1>
          <p>{t('oauth.subtitle')}</p>
        </div>
      </div>
    </div>
  );
};

export default OAuth2Callback;

