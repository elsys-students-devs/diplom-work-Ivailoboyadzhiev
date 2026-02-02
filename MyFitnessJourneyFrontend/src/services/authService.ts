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
  streak?: number;
  pictureUrl?: string | null;
}

export interface UpdateProfileRequest {
  username?: string;
  email?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
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
  localStorage.removeItem('access_token');
};

export const updateProfile = async (data: UpdateProfileRequest): Promise<UserDto> => {
  const response = await api.put<UserDto>('/auth/profile', data);
  return response.data;
};

export const changePassword = async (data: ChangePasswordRequest): Promise<UserDto> => {
  const response = await api.put<UserDto>('/auth/password', data);
  return response.data;
};

export const uploadProfilePicture = async (file: File): Promise<UserDto> => {
  const formData = new FormData();
  formData.append('file', file);
  // Use axios directly without api instance so Content-Type is not application/json.
  // Browser will set multipart/form-data with boundary automatically.
  const response = await axios.post<UserDto>(`${API_BASE_URL}/auth/profile/picture`, formData, {
    withCredentials: true,
  });
  return response.data;
};

/** Build URL for profile picture. OAuth URLs as-is; uploaded pics use relative path so they load from same origin (proxy/nginx). */
export const getProfilePictureUrl = (pictureUrl: string | null | undefined): string | null => {
  if (!pictureUrl || !pictureUrl.trim()) return null;
  if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) return pictureUrl;
  // Relative path so img loads from same host (dev proxy / production nginx forward /api to backend)
  return `/api/uploads/${pictureUrl}`;
};

export default api;

