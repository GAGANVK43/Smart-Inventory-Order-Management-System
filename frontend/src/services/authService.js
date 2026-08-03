import axiosInstance from '../api/axiosInstance';

export const login = async (usernameOrEmail, password) => {
  const response = await axiosInstance.post('/auth/login', { usernameOrEmail, password });
  if (response.data && response.data.data) {
    const { accessToken, refreshToken, username, email, roles } = response.data.data;
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    const user = { username, email, roles };
    localStorage.setItem('user', JSON.stringify(user));
    return { user, accessToken, refreshToken };
  }
  throw new Error(response.data.message || 'Login failed');
};

export const register = async (userData) => {
  const response = await axiosInstance.post('/auth/register', userData);
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await axiosInstance.get('/auth/me');
  return response.data.data;
};

export const logout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
};
