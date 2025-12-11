/// <reference types="vite/client" />
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  (import.meta.env.PROD ? '/api' : 'http://localhost:8080/api');

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable cookies/sessions
});

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user?: {
    id: number;
    email: string;
  };
  message?: string;
}

export const login = async (email: string, password: string): Promise<LoginResponse> => {
  const response = await api.post<LoginResponse>('/auth/login', {
    email,
    password,
  });
  // Session is automatically handled by cookies
  return response.data;
};

export const register = async (email: string, password: string): Promise<LoginResponse> => {
  const response = await api.post<LoginResponse>('/auth/register', {
    email,
    password,
  });
  // Session is automatically handled by cookies
  return response.data;
};

export const loginWithGoogle = () => {
  window.location.href = `${BACKEND_URL}/oauth2/authorization/google`;
};

export const loginWithFacebook = () => {
  window.location.href = `${BACKEND_URL}/oauth2/authorization/facebook`;
};

export const logout = async () => {

  localStorage.removeItem('access_token'); // Remove any leftover token
};

export default api;

