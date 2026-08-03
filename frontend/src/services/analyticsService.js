import axiosInstance from '../api/axiosInstance';

export const getDashboardStats = async () => {
  const response = await axiosInstance.get('/analytics/dashboard');
  return response.data.data;
};

export const getTopSellingProducts = async (limit = 5) => {
  const response = await axiosInstance.get(`/analytics/top-selling?limit=${limit}`);
  return response.data.data;
};

export const getRevenueTrends = async () => {
  const response = await axiosInstance.get('/analytics/revenue-trends');
  return response.data.data;
};

export const getStockHealthSummary = async () => {
  const response = await axiosInstance.get('/analytics/stock-health');
  return response.data.data;
};
