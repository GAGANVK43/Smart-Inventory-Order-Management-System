import api from './api';

const extractData = (res) => (res && res.data !== undefined ? res.data : res);

export const orderApi = {
  getAll: async () => {
    const response = await api.get('/orders');
    return extractData(response.data);
  },
  getById: async (id) => {
    const response = await api.get(`/orders/${id}`);
    return extractData(response.data);
  },
  create: async (data) => {
    const response = await api.post('/orders', data);
    return extractData(response.data);
  },
  updateStatus: async (id, status) => {
    const response = await api.patch(`/orders/${id}/status?status=${status}`);
    return extractData(response.data);
  },
  cancel: async (id) => {
    const response = await api.post(`/orders/${id}/cancel`);
    return extractData(response.data);
  },
};
