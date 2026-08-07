import api from './api';

const extractData = (res) => (res && res.data !== undefined ? res.data : res);

export const productApi = {
  getAll: async () => {
    const response = await api.get('/products');
    return extractData(response.data);
  },
  getById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return extractData(response.data);
  },
  search: async (keyword) => {
    const response = await api.get(`/products/search?keyword=${encodeURIComponent(keyword)}`);
    return extractData(response.data);
  },
  getByCategory: async (categoryId) => {
    const response = await api.get(`/products/category/${categoryId}`);
    return extractData(response.data);
  },
  getBySupplier: async (supplierId) => {
    const response = await api.get(`/products/supplier/${supplierId}`);
    return extractData(response.data);
  },
  getByPriceRange: async (min, max) => {
    const response = await api.get(`/products/price-range?min=${min}&max=${max}`);
    return extractData(response.data);
  },
  create: async (data) => {
    const response = await api.post('/products', data);
    return extractData(response.data);
  },
  update: async (id, data) => {
    const response = await api.put(`/products/${id}`, data);
    return extractData(response.data);
  },
  delete: async (id) => {
    const response = await api.delete(`/products/${id}`);
    return extractData(response.data);
  },
};
