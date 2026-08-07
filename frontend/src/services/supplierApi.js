import api from './api';

const extractData = (res) => (res && res.data !== undefined ? res.data : res);

export const supplierApi = {
  getAll: async () => {
    const response = await api.get('/suppliers');
    return extractData(response.data);
  },
  getById: async (id) => {
    const response = await api.get(`/suppliers/${id}`);
    return extractData(response.data);
  },
  create: async (data) => {
    const response = await api.post('/suppliers', data);
    return extractData(response.data);
  },
  update: async (id, data) => {
    const response = await api.put(`/suppliers/${id}`, data);
    return extractData(response.data);
  },
  delete: async (id) => {
    const response = await api.delete(`/suppliers/${id}`);
    return extractData(response.data);
  },
};
