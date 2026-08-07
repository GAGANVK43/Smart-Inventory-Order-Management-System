import api from './api';

const extractData = (res) => (res && res.data !== undefined ? res.data : res);

export const categoryApi = {
  getAll: async () => {
    const response = await api.get('/categories');
    return extractData(response.data);
  },
  getById: async (id) => {
    const response = await api.get(`/categories/${id}`);
    return extractData(response.data);
  },
  create: async (data) => {
    const response = await api.post('/categories', data);
    return extractData(response.data);
  },
  update: async (id, data) => {
    const response = await api.put(`/categories/${id}`, data);
    return extractData(response.data);
  },
  delete: async (id) => {
    const response = await api.delete(`/categories/${id}`);
    return extractData(response.data);
  },
};
