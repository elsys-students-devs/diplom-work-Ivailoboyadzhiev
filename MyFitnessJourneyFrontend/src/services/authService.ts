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
  username?: string;
}

export interface LoginResponse {
  user?: {
    id: number;
    email: string;
    username?: string;
    name?: string;
  };
  message?: string;
}

export interface UserDto {
  id: number;
  email: string;
  username?: string | null;
  name?: string | null;
}

export const login = async (email: string, password: string): Promise<LoginResponse> => {
  const response = await api.post<LoginResponse>('/auth/login', {
    email,
    password,
  });
  // Session is automatically handled by cookies
  return response.data;
};

export const register = async (email: string, password: string, username: string): Promise<LoginResponse> => {
  const response = await api.post<LoginResponse>('/auth/register', {
    email,
    password,
    username,
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

export const getCurrentUser = async (): Promise<UserDto | null> => {
  try {
    const response = await api.get<UserDto>('/auth/me');
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 401) {
      // User is not authenticated
    }
    return null;
  }
};

export const logout = async () => {
  localStorage.removeItem('access_token'); // Remove any leftover token
};

export default api;

