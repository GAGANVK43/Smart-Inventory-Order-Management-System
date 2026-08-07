import api from './api';

const extractData = (res) => (res && res.data !== undefined ? res.data : res);

export const customerApi = {
  getAll: async () => {
    const response = await api.get('/customers');
    return extractData(response.data);
  },
  getById: async (id) => {
    const response = await api.get(`/customers/${id}`);
    return extractData(response.data);
  },
  create: async (data) => {
    const response = await api.post('/customers', data);
    return extractData(response.data);
  },
  update: async (id, data) => {
    const response = await api.put(`/customers/${id}`, data);
    return extractData(response.data);
  },
  delete: async (id) => {
    const response = await api.delete(`/customers/${id}`);
    return extractData(response.data);
  },
};
