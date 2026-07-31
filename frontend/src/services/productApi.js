import api from './api';

export const productApi = {
  getAll: async () => {
    const response = await api.get('/products');
    return response.data;
  },
  getById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },
  search: async (keyword) => {
    const response = await api.get(`/products/search?keyword=${encodeURIComponent(keyword)}`);
    return response.data;
  },
  getByCategory: async (categoryId) => {
    const response = await api.get(`/products/category/${categoryId}`);
    return response.data;
  },
  getBySupplier: async (supplierId) => {
    const response = await api.get(`/products/supplier/${supplierId}`);
    return response.data;
  },
  getByPriceRange: async (min, max) => {
    const response = await api.get(`/products/price-range?min=${min}&max=${max}`);
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/products', data);
    return response.data;
  },
  update: async (id, data) => {
    const response = await api.put(`/products/${id}`, data);
    return response.data;
  },
  delete: async (id) => {
    const response = await api.delete(`/products/${id}`);
    return response.data;
  },
};
