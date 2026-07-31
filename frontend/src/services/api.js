import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Interceptor for standardized error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    let message = 'An unexpected error occurred';
    if (error.response) {
      // Backend returned custom ErrorDetails payload
      const data = error.response.data;
      message = data.message || data.error || message;
    } else if (error.request) {
      message = 'Unable to connect to Spring Boot backend. Is MySQL and server running?';
    }
    return Promise.reject(new Error(message));
  }
);

export default api;
